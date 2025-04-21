package dev.shchuko.marinescreen.data.station.windguru

import android.util.Log
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.StationRepository
import dev.shchuko.marinescreen.domain.model.StationMeasurement
import dev.shchuko.marinescreen.domain.model.StationMeasurements
import dev.shchuko.marinescreen.domain.model.StationError
import dev.shchuko.marinescreen.domain.model.StationErrorKind
import dev.shchuko.marinescreen.domain.model.WindGuruSettings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Instant
import kotlinx.io.IOException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class WindGuruStationRepository(
    coroutineScope: CoroutineScope,
    private val settingsRepository: SettingsRepository,
    private val preciseTimeProvider: PreciseTimeProvider,
    private val timeWindow: Duration = 4.hours,
    private val refreshInterval: Duration = 1.minutes,
    private val currentMeasurementMaxAge: Duration = 3.minutes,
) : StationRepository {
    companion object {
        private const val LOG_TAG = "WindGuruStation"
    }

    private val measurementUpdateMutex = Mutex()
    private val _measurements = MutableStateFlow(StationMeasurements())
    override val station: StateFlow<StationMeasurements> = _measurements

    private val relaxedJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    private val httpClient = HttpClient(Android) {
        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 5.seconds.inWholeMilliseconds
            requestTimeoutMillis = 5.seconds.inWholeMilliseconds
            socketTimeoutMillis = 5.seconds.inWholeMilliseconds
        }
    }

    init {
        coroutineScope.launch {
            settingsRepository.stationSettingsFlow.collectLatest { settings ->
                preciseTimeProvider.firstNtpSyncDone.collectLatest {
                    runRefreshLoop(settings)
                }
            }
        }
    }

    private suspend fun runRefreshLoop(
        settings: WindGuruSettings
    ) = coroutineScope {
        Log.d(LOG_TAG, "Starting refresh loop uid=${settings.windGuruUid}")

        _measurements.value = StationMeasurements(stationName = settings.stationName)

        while (isActive) {
            Log.d(LOG_TAG, "Starting next refresh loop cycle uid=${settings.windGuruUid}")
            val prevMeasurements = _measurements.value

            val now = preciseTimeProvider.getCurrent().time
            val timeWindowStart = now - timeWindow

            val fetchFrom = listOfNotNull(
                // fetch data since last known measurement
                prevMeasurements.historical.maxOfOrNull { it.timestamp },
                // but not earlier than [timeWindowStart]
                timeWindowStart,
            ).max()

            var error: StationError? = null
            val fetchResult = try {
                if (settings.windGuruUid.isEmpty()) {
                    emptyList()
                } else {
                    fetchWindGuruData(
                        stationUid = settings.windGuruUid,
                        stationPassword = settings.windGuruPassword,
                        from = fetchFrom,
                        to = now,
                        intervalMinutes = 1,
                    )
                }
            } catch (e: StationError) {
                error = e
                null
            }

            Log.d(
                LOG_TAG,
                "Fetch done uid=${settings.windGuruUid} old=${prevMeasurements.historical.size} new=${fetchResult?.size} errorKind=${error?.kind} errorMessage=${error?.message}"
            )
            val merged = mergeMeasurements(
                from = timeWindowStart,
                to = now,
                old = prevMeasurements.historical,
                new = fetchResult ?: emptyList(),
            )

            Log.d(
                LOG_TAG,
                "Measurements merge done uid=${settings.windGuruUid} size=${merged.size}"
            )

            // Only one (the latest) Flow.collectLatest {} wins and updates the state
            measurementUpdateMutex.withLock {
                Log.d(LOG_TAG, "Updating state uid=${settings.windGuruUid} size=${merged.size}")
                _measurements.value = StationMeasurements(
                    stationName = settings.stationName,
                    current = getCurrentMeasurement(merged, now),
                    historical = merged,
                    error = error,
                    lastUpdatedAt = if (error != null) prevMeasurements.lastUpdatedAt else now,
                )
                Log.d(LOG_TAG, "Update state done uid=${settings.windGuruUid} size=${merged.size}")

            }
            delay(refreshInterval)
        }

        while (isActive) {
            measurementUpdateMutex.withLock {
                val currentValue = _measurements.value
                val now = preciseTimeProvider.getCurrent().time
                if (currentValue.current?.canBeCurrent(now) == false) {
                    Log.d(
                        LOG_TAG,
                        "Resetting 'current' measurement uid=${settings.windGuruUid} current=${currentValue.current?.timestamp} now=$now"
                    )
                    _measurements.value = currentValue.copy(current = null)
                }
            }
            delay(1.seconds)
        }
    }

    /**
     * [old] and [new] must be sorted
     */
    private fun mergeMeasurements(
        from: Instant,
        to: Instant,
        old: List<StationMeasurement>,
        new: List<StationMeasurement>,
    ): List<StationMeasurement> {
        val result = ArrayList<StationMeasurement>(old.size + new.size)
        val maxTimestampFromOldList = old.maxOfOrNull { it.timestamp } ?: Instant.DISTANT_PAST

        result.addAll(
            old.filter { measurement ->
                // filter measurements only from [from..to] range
                measurement.timestamp in from..to
            }
        )

        result.addAll(
            new.filter { measurement ->
                // filter measurements only from [from..to] range
                measurement.timestamp in from..to
                        // and prevent adding the same measurements which already present in [old]
                        && measurement.timestamp > maxTimestampFromOldList
            }
        )

        return result
    }

    private fun getCurrentMeasurement(
        measurements: List<StationMeasurement>,
        now: Instant
    ): StationMeasurement? =
        measurements.maxByOrNull { it.timestamp }?.takeIf { it.canBeCurrent(now) }

    private fun StationMeasurement.canBeCurrent(now: Instant): Boolean =
        timestamp >= now - currentMeasurementMaxAge


    private suspend fun fetchWindGuruData(
        stationUid: String,
        stationPassword: String,
        from: Instant,
        to: Instant,
        intervalMinutes: Int = 1
    ): List<StationMeasurement> = try {
        Log.d(
            LOG_TAG,
            "Fetching WindGuru data uid=${stationUid} from=${from} to=${to} interval=${intervalMinutes}"
        )

        val response = httpClient.get {
            url("https://www.windguru.cz/int/wgsapi.php")
            parameter("uid", stationUid)
            parameter("password", stationPassword)
            parameter("q", "station_data")
            parameter("from", from)
            parameter("to", to)
            parameter("avg_minutes", "$intervalMinutes")
            parameter(
                "vars",
                listOf(
                    "unixtime",
                    "wind_avg",
                    "wind_max",
                    "wind_direction",
                    "temperature",
                    "rh",
                ).joinToString(separator = ","),
            )
        }

        val bodyString = response.body<String>()
        if (response.status.isSuccess()) {
            Log.d(LOG_TAG, "WindGuru data fetch OK, parsing body uid=${stationUid}")
            val parsed = parseWindGuruResponse<WindGuruMeasurementsResponseDto>(bodyString)
            val measurements = parsed.toModel()
            Log.d(
                LOG_TAG,
                "WindGuru data fetch OK, body parsed uid=${stationUid} measurements=${measurements.size}"
            )
            measurements
        } else {
            Log.d(LOG_TAG, "WindGuru data fetch failed, parsing error uid=${stationUid}")
            val parsed = parseWindGuruResponse<WindGuruErrorResponseDto>(bodyString)
            val throwable = parsed.toThrowable()
            Log.d(
                LOG_TAG,
                "WindGuru data fetch failed, error parsed uid=${stationUid} kind=${throwable.kind} message=${throwable.message}"
            )
            throw throwable
        }
    } catch (ce: CancellationException) {
        throw ce
    } catch (e: StationError) {
        throw e
    } catch (e: IOException) {
        Log.d(LOG_TAG, "WindGuru data fetch failed, IOException uid=${stationUid}", e)
        throw StationError(StationErrorKind.CONNECTION_ERROR, cause = e)
    } catch (e: Exception) {
        Log.d(LOG_TAG, "WindGuru data fetch failed, unknown exception uid=${stationUid}", e)
        throw StationError(StationErrorKind.INTERNAL_ERROR, cause = e)
    }

    // WindGuru may respond with empty list [] instead of empty json {}
    private inline fun <reified T> parseWindGuruResponse(response: String): T =
        relaxedJson.decodeFromString<T>(if (response == "[]") "{}" else response)

    private fun WindGuruErrorResponseDto.toThrowable() = StationError(
        message = errorMessage,
        kind = when {
            errorMessage.isNullOrEmpty() -> {
                StationErrorKind.UNKNOWN
            }

            errorMessage.contains("wrong station login", ignoreCase = true) -> {
                StationErrorKind.WRONG_STATION_LOGIN
            }

            errorMessage.contains("unknown station", ignoreCase = true) -> {
                StationErrorKind.UNKNOWN_STATION
            }

            errorMessage.contains("unknown query", ignoreCase = true) -> {
                StationErrorKind.UNKNOWN_QUERY
            }

            else -> {
                StationErrorKind.UNKNOWN
            }
        },
    )

    private fun WindGuruMeasurementsResponseDto.toModel(): List<StationMeasurement> {
        if (timestamp == null) {
            return emptyList()
        }

        val measurementsTotal = timestamp.size
        val notSortedResult = ArrayList<StationMeasurement>(measurementsTotal)
        repeat(measurementsTotal) { i ->
            notSortedResult.add(
                StationMeasurement(
                    timestamp = Instant.fromEpochSeconds(timestamp[i]),
                    temperatureC = temperature?.get(i),
                    windSpeedKts = windAvg?.get(i),
                    windGustKts = windMax?.get(i),
                    windDirectionDeg = windDirection?.get(i)?.roundToInt(),
                    humidityPercent = rh?.get(i)?.roundToInt(),
                )
            )
        }
        return notSortedResult.sortedBy { it.timestamp }
    }

    @Serializable
    private data class WindGuruErrorResponseDto(
        @SerialName("error_message")
        val errorMessage: String? = null,
    )

    @Serializable
    private data class WindGuruMeasurementsResponseDto(
        @SerialName("unixtime")
        val timestamp: List<Long>? = null,
        @SerialName("wind_avg")
        val windAvg: List<Double?>? = null,
        @SerialName("wind_max")
        val windMax: List<Double?>? = null,
        @SerialName("wind_direction")
        val windDirection: List<Double?>? = null,
        @SerialName("temperature")
        val temperature: List<Double?>? = null,
        @SerialName("rh")
        val rh: List<Double?>? = null,
    )
}

