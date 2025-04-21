package dev.shchuko.marinescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.model.PreciseTime
import dev.shchuko.marinescreen.domain.model.WindGuruSettings
import dev.shchuko.marinescreen.domain.usecase.AcceptTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationMeasurementsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationSettingsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveTermsAcceptedUseCase
import dev.shchuko.marinescreen.domain.usecase.RejectTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.UpdateStationSettingsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val acceptTermsUseCase: AcceptTermsUseCase,
    private val rejectTermsUseCase: RejectTermsUseCase,
    observeTermsAcceptedUseCase: ObserveTermsAcceptedUseCase,
    observeStationSettings: ObserveStationSettingsUseCase,
    observeStationMeasurements: ObserveStationMeasurementsUseCase,
    private val updateStationSettings: UpdateStationSettingsUseCase,
    private val timeProvider: PreciseTimeProvider,
) : ViewModel() {

    private val _time = MutableStateFlow(timeProvider.getCurrent())
    val time: StateFlow<PreciseTime> = _time
    val firstNtpSyncDone = timeProvider.firstNtpSyncDone

    val termsAccepted = observeTermsAcceptedUseCase()
    val stationMeasurements = observeStationMeasurements()
    val stationSettings = observeStationSettings()

    init {
        viewModelScope.launch {
            timeProvider.firstNtpSyncDone.collectLatest {
                while (isActive) {
                    _time.value = timeProvider.getCurrent()
                    delay(1000)
                }
            }
        }
    }

    fun acceptTerms() {
        acceptTermsUseCase()
    }

    fun rejectTerms() {
        rejectTermsUseCase()
    }

    fun saveStationSettings(settings: WindGuruSettings) {
        updateStationSettings(settings)
    }
}
