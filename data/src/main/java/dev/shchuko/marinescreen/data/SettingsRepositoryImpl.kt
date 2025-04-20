@file:Suppress("SameParameterValue")

package dev.shchuko.marinescreen.data


import android.content.Context
import androidx.core.content.edit
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.InvalidSettingsException
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "app_prefs"

        private const val KEY_TERMS = "terms_accepted"
        private const val KEY_NAME = "station_name"
        private const val KEY_UID = "station_uid"
        private const val KEY_PASSWORD = "station_password"
    }

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _termsAcceptedFlow = MutableStateFlow(prefs.getBoolean(KEY_TERMS, false))
    override val termsAcceptedFlow: StateFlow<Boolean> = _termsAcceptedFlow

    private val _stationSettingsFlow = MutableStateFlow(readStationSettings())
    override val stationSettingsFlow: StateFlow<WeatherStationSettings> = _stationSettingsFlow

    override suspend fun setTermsAccepted() {
        prefs.edit { putBoolean(KEY_TERMS, true) }
        _termsAcceptedFlow.value = true
    }

    override suspend fun setTermsRejected() {
        prefs.edit { putBoolean(KEY_TERMS, false) }
        _termsAcceptedFlow.value = false
    }

    override suspend fun updateStationSettings(settings: WeatherStationSettings) {
        prefs.edit {
            putString(KEY_NAME, settings.displayName)
            putString(KEY_UID, settings.stationUid)
            putString(KEY_PASSWORD, settings.stationPassword)
        }
        _stationSettingsFlow.value = readStationSettings()
    }

    private fun readStationSettings(): WeatherStationSettings = try {
        WeatherStationSettings(
            displayName = prefs.getString(KEY_NAME, "") ?: "",
            stationUid = prefs.getString(KEY_UID, "") ?: "",
            stationPassword = prefs.getString(KEY_PASSWORD, "") ?: ""
        )
    } catch (e: InvalidSettingsException) {
        WeatherStationSettings.NOT_SET
    }
}

