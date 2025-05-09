package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.WindGuruSettings
import kotlinx.coroutines.flow.StateFlow

class ObserveStationSettingsUseCase(
    private val repo: SettingsRepository,
) {
    operator fun invoke(): StateFlow<WindGuruSettings> = repo.stationSettingsFlow
}

class UpdateStationSettingsUseCase(
    private val repo: SettingsRepository
) {
    operator fun invoke(newSettings: WindGuruSettings) {
        repo.updateStationSettings(newSettings)
    }
}

class ObserveScreenScaleSettingUseCase(
    private val repo: SettingsRepository,
) {
    operator fun invoke(): StateFlow<Float> = repo.screenScaleFlow
}

class UpdateScreenScaleSettingUseCase(
    private val repo: SettingsRepository
) {
    operator fun invoke(newScale: Float) {
        repo.updateScreenScale(newScale)
    }
}