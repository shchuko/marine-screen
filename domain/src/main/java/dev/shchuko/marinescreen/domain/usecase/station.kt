package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.StationRepository
import dev.shchuko.marinescreen.domain.model.StationMeasurements
import kotlinx.coroutines.flow.StateFlow


class ObserveStationMeasurementsUseCase(
    private val repo: StationRepository,
) {
    operator fun invoke(): StateFlow<StationMeasurements> = repo.station
}
