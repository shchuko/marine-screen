package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import kotlinx.coroutines.flow.StateFlow

class ObserveStationSettingsUseCase(
    private val repo: SettingsRepository,
) {
    operator fun invoke(): StateFlow<WeatherStationSettings> = repo.stationSettingsFlow
}

class UpdateStationSettingsUseCase(
    private val repo: SettingsRepository
) {
    operator fun invoke(newSettings: WeatherStationSettings) {
        repo.updateStationSettings(newSettings)
    }
}