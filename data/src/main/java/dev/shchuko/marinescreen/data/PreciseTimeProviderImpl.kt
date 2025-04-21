package dev.shchuko.marinescreen.data

import android.util.Log
import dev.shchuko.marinescreen.domain.model.PreciseTime
import dev.shchuko.marinescreen.domain.NtpClient
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.model.PreciseTimeStatus
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class PreciseTimeProviderImpl(
    private val provider: NtpClient,
    coroutineScope: CoroutineScope,
    private val ntpRefreshInterval: Duration = 2.minutes,
    private val ntpRefreshErrorRetryInitialDelay: Duration = 10.seconds,
    private val ntpRefreshErrorRetryMaxDelay: Duration = 1.minutes,
    private val ntpStaleThreshold: Duration = 5.minutes,
    private val systemTimeDriftWarningThreshold: Duration = 5.minutes,
) : PreciseTimeProvider {
    companion object {
        private const val LOG_TAG = "PreciseTimeProvider"
    }

    private data class NtpTimeSnapshot(val ntpTime: Instant, val nanoTime: Long)

    @Volatile
    private var snapshot: NtpTimeSnapshot? = null

    init {
        coroutineScope.launch {
            Log.d(LOG_TAG, "Starting ntp refresh loop")
            var retryDelay = ntpRefreshErrorRetryInitialDelay
            while (isActive) {
                Log.d(LOG_TAG, "Starting next ntp refresh loop iteration")

                val success = try {
                    val ntpTime = NtpTimeSnapshot(provider.getCurrent(), System.nanoTime())
                    snapshot = ntpTime
                    Log.d(LOG_TAG, "NTP refresh OK, ntp=${ntpTime.ntpTime} system=${Clock.System.now()} systemNano=${System.nanoTime()}")
                    true
                } catch (e: Exception) {
                    Log.d(LOG_TAG, "NTP refresh error", e)
                    false
                }

                if (success) {
                    retryDelay = ntpRefreshErrorRetryInitialDelay
                    Log.d(LOG_TAG, "NTP refresh successful, retrying in $ntpRefreshInterval")
                    delay(ntpRefreshInterval)
                } else {
                    Log.d(LOG_TAG, "NTP refresh failed, retrying in $retryDelay")
                    delay(retryDelay)
                    val jitter = Random.nextDouble(0.85, 1.15)
                    retryDelay = (retryDelay * 2 * jitter).coerceAtMost(ntpRefreshErrorRetryMaxDelay)
                }
            }
        }
    }

    override fun getCurrent(): PreciseTime {
        val snap = snapshot ?: return systemOnlyTime()
        val elapsed = (System.nanoTime() - snap.nanoTime).nanoseconds
        val ntpNow = snap.ntpTime + elapsed
        val sysNow = Clock.System.now()
        val sysDrift = (sysNow - ntpNow).absoluteValue

        return PreciseTime(
            status = when {
                elapsed > ntpStaleThreshold -> {
                    PreciseTimeStatus.NTP_STALE
                }

                sysDrift > systemTimeDriftWarningThreshold -> {
                    PreciseTimeStatus.SYSTEM_TIME_DRIFT_WARNING
                }

                else -> {
                    PreciseTimeStatus.OK
                }
            },
            ntpTime = ntpNow,
            ntpLastUpdated = elapsed,
            systemTime = sysNow,
        )
    }

    private fun systemOnlyTime(): PreciseTime = PreciseTime(
        status = PreciseTimeStatus.SYSTEM_TIME_ONLY,
        ntpTime = null,
        ntpLastUpdated = null,
        systemTime = Clock.System.now(),
    )
}
