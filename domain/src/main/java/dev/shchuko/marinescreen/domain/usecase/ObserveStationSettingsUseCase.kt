package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import kotlinx.coroutines.flow.StateFlow

interface ObserveStationSettingsUseCase {
    operator fun invoke(): StateFlow<WeatherStationSettings>
}