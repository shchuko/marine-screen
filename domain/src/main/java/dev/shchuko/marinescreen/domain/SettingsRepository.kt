package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val termsAcceptedFlow: StateFlow<Boolean>
    val stationSettingsFlow: StateFlow<WeatherStationSettings>

    suspend fun setTermsAccepted()
    suspend fun setTermsRejected()

    suspend fun updateStationSettings(settings: WeatherStationSettings)
}