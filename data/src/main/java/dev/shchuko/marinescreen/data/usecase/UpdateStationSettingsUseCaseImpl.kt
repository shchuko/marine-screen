package dev.shchuko.marinescreen.data.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import dev.shchuko.marinescreen.domain.usecase.UpdateStationSettingsUseCase

class UpdateStationSettingsUseCaseImpl(
    private val repo: SettingsRepository
) : UpdateStationSettingsUseCase {
    override suspend fun invoke(newSettings: WeatherStationSettings) {
        repo.updateStationSettings(newSettings)
    }
}