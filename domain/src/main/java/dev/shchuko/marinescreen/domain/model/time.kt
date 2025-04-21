package dev.shchuko.marinescreen.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

data class PreciseTime(
    val status: PreciseTimeStatus,
    val ntpTime: Instant?,
    val ntpLastUpdated: Duration?,

    val systemTime: Instant,
) {
    val time: Instant = ntpTime ?: systemTime
    val localTime: LocalDateTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
}

enum class PreciseTimeStatus {
    OK,
    SYSTEM_TIME_DRIFT_WARNING,
    SYSTEM_TIME_ONLY,
    NTP_STALE,
    ;
}