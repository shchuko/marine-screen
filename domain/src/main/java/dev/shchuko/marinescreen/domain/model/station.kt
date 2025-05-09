package dev.shchuko.marinescreen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.minutes

data class StationMeasurements(
    val stationName: String? = null,
    val current: StationMeasurement? = null,
    val historical: List<StationMeasurement> = emptyList(),
    val error: StationErrorException? = null,
    val lastUpdatedAt: Instant? = null,
) {
    val lastMeasurementAt: Instant? = historical.lastOrNull()?.timestamp
}

data class StationMeasurement(
    val timestamp: Instant,
    val windSpeedKts: Double?,
    val windGustKts: Double?,
    val windDirectionDeg: Int?,
    val temperatureC: Double?,
    val humidityPercent: Int?,
) {
    val windSpeedMps: Double? = windSpeedKts?.times(0.514444f)
    val windGustMps: Double? = windGustKts?.times(0.514444f)
    val temperatureF: Double? = temperatureC?.times(1.8f)?.plus(32)
}


sealed interface StationError {
    data class Unknown(val message: String? = null) : StationError
    data class InternalError(val message: String? = null) : StationError
    data class ConnectionError(val provider: String = "WindGuru", val message: String? = null) : StationError
    data class UnknownStation(val station: String) : StationError
    data class WrongStationLogin(val station: String) : StationError
}

class StationErrorException(
    val details: StationError = StationError.Unknown(),
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

// TODO ---------

private val now = Clock.System.now()

val stub = StationMeasurements(
    current = StationMeasurement(
        timestamp = now,
        windSpeedKts = 12.0,
        windGustKts = 18.0,
        windDirectionDeg = 135,
        temperatureC = 22.0,
        humidityPercent = 55
    ),
    historical = List(60) { index ->
        val time = now - ((59 - index) * 10).minutes
        val t = index / 59 * 2 * PI

        // varies between 6 and 14 kts
        val windSpeedKts = 10 + 4 * sin(t)

        StationMeasurement(
            timestamp = time,
            windSpeedKts = windSpeedKts,
            // extra bumpiness in gusts
            windGustKts = windSpeedKts + 2 + sin(t * 2),
            // varies slowly +/- 30 deg
            windDirectionDeg = (120 + 30 * sin(t / 2).toInt()) % 360,
            // varies between 19–23 °C
            temperatureC = 21 + 2 * cos(t),
            // between ~50–70%
            humidityPercent = (60 + 10 * sin(t + PI / 4)).roundToInt(),
        )
    },
    lastUpdatedAt = now
)