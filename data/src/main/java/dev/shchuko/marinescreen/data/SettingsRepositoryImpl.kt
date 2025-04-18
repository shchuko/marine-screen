package dev.shchuko.marinescreen.data

import android.content.Context
import android.content.SharedPreferences
import dev.shchuko.marinescreen.domain.SettingsRepository
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _termsAcceptedFlow = MutableStateFlow(prefs.getBoolean("terms_accepted", false))

    override val termsAcceptedFlow: StateFlow<Boolean> = _termsAcceptedFlow

    override suspend fun setTermsAccepted() {
        prefs.edit { putBoolean("terms_accepted", true) }
        _termsAcceptedFlow.value = true
    }

    override suspend fun setTermsRejected() {
        prefs.edit { putBoolean("terms_accepted", false) }
        _termsAcceptedFlow.value = false
    }
}