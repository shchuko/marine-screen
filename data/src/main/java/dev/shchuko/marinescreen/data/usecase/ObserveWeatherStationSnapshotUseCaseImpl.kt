package dev.shchuko.marinescreen.data.usecase;

import dev.shchuko.marinescreen.domain.model.StationSnapshot
import dev.shchuko.marinescreen.domain.repository.StationRepository;
import dev.shchuko.marinescreen.domain.usecase.ObserveWeatherStationSnapshotUseCase
import kotlinx.coroutines.flow.StateFlow

class ObserveWeatherStationSnapshotUseCaseImpl(
    private val repo: StationRepository,
) : ObserveWeatherStationSnapshotUseCase {
    override fun invoke(): StateFlow<StationSnapshot?> = repo.station
}
