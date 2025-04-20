package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.model.StationSnapshot
import kotlinx.coroutines.flow.StateFlow

interface ObserveWeatherStationSnapshotUseCase {
    operator fun invoke(): StateFlow<StationSnapshot?>
}