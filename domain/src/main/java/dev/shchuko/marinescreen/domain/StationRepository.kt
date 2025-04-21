package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.StationSnapshot
import kotlinx.coroutines.flow.StateFlow

interface StationRepository {
    val station: StateFlow<StationSnapshot?>
    suspend fun refresh()
}
