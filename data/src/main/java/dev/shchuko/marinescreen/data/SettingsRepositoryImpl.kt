@file:Suppress("SameParameterValue")

package dev.shchuko.marinescreen.data


import android.content.Context
import androidx.core.content.edit
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.InvalidSettingsException
import dev.shchuko.marinescreen.domain.model.WindGuruSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "app_prefs"

        private const val KEY_TERMS = "terms_accepted"
        private const val KEY_NAME = "station_name"
        private const val KEY_UID = "station_uid"
        private const val KEY_PASSWORD = "station_password"
        private const val KEY_SCREEN_SCALE = "screen_scale"
    }

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _termsAcceptedFlow = MutableStateFlow(prefs.getBoolean(KEY_TERMS, false))
    override val termsAcceptedFlow: StateFlow<Boolean> = _termsAcceptedFlow

    private val _stationSettingsFlow = MutableStateFlow(readStationSettings())
    override val stationSettingsFlow: StateFlow<WindGuruSettings> = _stationSettingsFlow

    private val _screenScaleFlow = MutableStateFlow(readScreenScale())
    override val screenScaleFlow: StateFlow<Float> = _screenScaleFlow

    override fun setTermsAccepted() {
        prefs.edit { putBoolean(KEY_TERMS, true) }
        _termsAcceptedFlow.value = true
    }

    override fun setTermsRejected() {
        prefs.edit { putBoolean(KEY_TERMS, false) }
        _termsAcceptedFlow.value = false
    }

    override fun updateStationSettings(settings: WindGuruSettings) {
        prefs.edit {
            putString(KEY_NAME, settings.stationName)
            putString(KEY_UID, settings.windGuruUid)
            putString(KEY_PASSWORD, settings.windGuruPassword)
        }
        _stationSettingsFlow.value = readStationSettings()
    }

    override fun updateScreenScale(scale: Float) {
        prefs.edit {
            putFloat(KEY_SCREEN_SCALE, scale)
        }
        _screenScaleFlow.value = readScreenScale()
    }

    private fun readStationSettings(): WindGuruSettings = try {
        WindGuruSettings(
            stationName = prefs.getString(KEY_NAME, "") ?: "",
            windGuruUid = prefs.getString(KEY_UID, "") ?: "",
            windGuruPassword = prefs.getString(KEY_PASSWORD, "") ?: ""
        )
    } catch (e: InvalidSettingsException) {
        WindGuruSettings.NOT_SET
    }

    private fun readScreenScale(): Float = prefs.getFloat(KEY_SCREEN_SCALE, 100f)
}

