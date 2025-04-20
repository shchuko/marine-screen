package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.model.WeatherStationSettings

interface UpdateStationSettingsUseCase {
    suspend operator fun invoke(newSettings: WeatherStationSettings)
}