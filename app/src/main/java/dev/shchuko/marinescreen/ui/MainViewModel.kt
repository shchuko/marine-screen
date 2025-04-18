package dev.shchuko.marinescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shchuko.marinescreen.domain.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val termsAccepted: StateFlow<Boolean> = settingsRepository.termsAcceptedFlow

    fun acceptTerms() {
        viewModelScope.launch {
            settingsRepository.setTermsAccepted()
        }
    }

    fun rejectTerms() {
        viewModelScope.launch {
            settingsRepository.setTermsRejected()
        }
    }
}
