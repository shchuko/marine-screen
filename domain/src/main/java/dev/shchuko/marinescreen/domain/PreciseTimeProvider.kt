package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.PreciseTime
import kotlinx.coroutines.flow.StateFlow

interface PreciseTimeProvider {
    val firstNtpSyncDone: StateFlow<Boolean>
    fun getCurrent(): PreciseTime
}