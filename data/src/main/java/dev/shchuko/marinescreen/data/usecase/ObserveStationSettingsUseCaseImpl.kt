package dev.shchuko.marinescreen.data.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import dev.shchuko.marinescreen.domain.usecase.ObserveStationSettingsUseCase
import kotlinx.coroutines.flow.StateFlow

class ObserveStationSettingsUseCaseImpl(
    private val repo: SettingsRepository,
) : ObserveStationSettingsUseCase {
    override fun invoke(): StateFlow<WeatherStationSettings> = repo.stationSettingsFlow
}