package dev.shchuko.marinescreen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class TimeDetails(
    val ntpTime: Instant?,
    val ntpLastUpdated: Duration?,
    val ntpStale: Boolean?,

    val systemTime: Instant,
    val systemTimeDrift: Duration?,
    val systemTimeDriftWarning: Boolean?,
) {
    companion object {
        fun systemOnly(): TimeDetails = TimeDetails(
            ntpTime = null,
            ntpLastUpdated = null,
            ntpStale = null,
            systemTime = Clock.System.now(),
            systemTimeDrift = null,
            systemTimeDriftWarning = null,
        )
    }
}
