package com.example.domain.time

import dev.shchuko.marinescreen.domain.NtpClient
import dev.shchuko.marinescreen.domain.model.TimeDetails
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class PreciseTimeProvider(
    private val provider: NtpClient,
    coroutineScope: CoroutineScope,
    private val ntpRefreshInterval: Duration = 2.minutes,
    private val ntpRefreshErrorRetryInitialDelay: Duration = 1.seconds,
    private val ntpRefreshErrorRetryMaxDelay: Duration = 1.minutes,
    private val ntpStaleThreshold: Duration = 10.minutes,
    private val systemTimeDriftWarningThreshold: Duration = 5.minutes,
) {
    private data class NtpTimeSnapshot(val ntpTime: Instant, val nanoTime: Long)

    @Volatile
    private var snapshot: NtpTimeSnapshot? = null

    init {
        coroutineScope.launch {
            var backoff = ntpRefreshErrorRetryInitialDelay
            while (isActive) {
                val success = try {
                    snapshot = NtpTimeSnapshot(provider.getCurrent(), System.nanoTime())
                    backoff = ntpRefreshErrorRetryInitialDelay
                    true
                } catch (e: Exception) {
                    false
                }

                val delayTime = if (success) ntpRefreshInterval
                else (backoff * Random.nextDouble(0.85, 1.15)).coerceAtMost(ntpRefreshErrorRetryMaxDelay)
                    .also { backoff = (backoff * 2).coerceAtMost(ntpRefreshErrorRetryMaxDelay) }

                delay(delayTime)
            }
        }
    }

    fun getCurrent(): TimeDetails {
        val snap = snapshot ?: return TimeDetails.systemOnly()
        val elapsed = (System.nanoTime() - snap.nanoTime).nanoseconds
        val ntpNow = snap.ntpTime + elapsed
        val sysNow = Clock.System.now()
        val drift = (sysNow - ntpNow).absoluteValue

        return TimeDetails(
            ntpTime = ntpNow,
            ntpLastUpdated = elapsed,
            ntpStale = elapsed > ntpStaleThreshold,
            systemTime = sysNow,
            systemTimeDrift = drift,
            systemTimeDriftWarning = drift > systemTimeDriftWarningThreshold,
        )
    }
}
