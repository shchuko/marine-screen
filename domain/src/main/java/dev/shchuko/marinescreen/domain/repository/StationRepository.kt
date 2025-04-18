package dev.shchuko.marinescreen.domain.repository

import dev.shchuko.marinescreen.domain.model.StationSnapshot
import kotlinx.coroutines.flow.StateFlow

interface StationRepository {
    val station: StateFlow<StationSnapshot?>
    suspend fun refresh()
}
