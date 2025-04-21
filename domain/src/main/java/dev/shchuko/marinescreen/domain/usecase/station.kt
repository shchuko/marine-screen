package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.StationRepository
import dev.shchuko.marinescreen.domain.model.StationSnapshot
import kotlinx.coroutines.flow.StateFlow


class ObserveWeatherStationSnapshotUseCase(
    private val repo: StationRepository,
) {
    operator fun invoke(): StateFlow<StationSnapshot?> = repo.station
}
