package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.StationMeasurements
import kotlinx.coroutines.flow.StateFlow

interface StationRepository {
    val station: StateFlow<StationMeasurements>
}
