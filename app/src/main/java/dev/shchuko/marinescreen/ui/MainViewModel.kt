package dev.shchuko.marinescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.StationSnapshot
import dev.shchuko.marinescreen.domain.repository.StationRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository,
) : ViewModel() {

    val termsAccepted: StateFlow<Boolean> = settingsRepository.termsAcceptedFlow
    val stationSnapshot: StateFlow<StationSnapshot?> = stationRepository.station

    fun acceptTerms() {
        viewModelScope.launch { settingsRepository.setTermsAccepted() }
    }

    fun rejectTerms() {
        viewModelScope.launch { settingsRepository.setTermsRejected() }
    }

    fun refreshStation() {
        viewModelScope.launch {
            stationRepository.refresh()
        }
    }
}
