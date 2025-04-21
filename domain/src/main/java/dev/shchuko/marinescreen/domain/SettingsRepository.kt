package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.WindGuruSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val termsAcceptedFlow: StateFlow<Boolean>
    val stationSettingsFlow: StateFlow<WindGuruSettings>

    fun setTermsAccepted()
    fun setTermsRejected()

    fun updateStationSettings(settings: WindGuruSettings)
}