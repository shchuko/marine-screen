package dev.shchuko.marinescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.StationSnapshot
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings
import dev.shchuko.marinescreen.domain.StationRepository
import dev.shchuko.marinescreen.domain.usecase.AcceptTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationSettingsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveTermsAcceptedUseCase
import dev.shchuko.marinescreen.domain.usecase.RejectTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.UpdateStationSettingsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val acceptTermsUseCase: AcceptTermsUseCase,
    private val rejectTermsUseCase: RejectTermsUseCase,
    observeTermsAcceptedUseCase: ObserveTermsAcceptedUseCase,
    observeStationSettings: ObserveStationSettingsUseCase,
    private val updateStationSettings: UpdateStationSettingsUseCase,
    private val stationRepository: StationRepository,
    private val timeProvider: PreciseTimeProvider,
) : ViewModel() {

    private val _time = MutableStateFlow("00/00/0000 00:00:00")
    val time: StateFlow<String> = _time

    val termsAccepted = observeTermsAcceptedUseCase()
    val stationSnapshot: StateFlow<StationSnapshot?> = stationRepository.station
    val stationSettings = observeStationSettings()

    init {
        viewModelScope.launch {
            while (true) {
                val instant = timeProvider.getCurrent().ntpTime ?: timeProvider.getCurrent().systemTime
                val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                _time.value = formatLocalDateTime(local)
                delay(1000)
            }
        }
    }

    fun acceptTerms() {
        acceptTermsUseCase()
    }

    fun rejectTerms() {
        rejectTermsUseCase()
    }

    fun saveStationSettings(settings: WeatherStationSettings) {
        updateStationSettings(settings)
    }

    fun refreshStation() {
        viewModelScope.launch {
            stationRepository.refresh()
        }
    }

    private fun formatLocalDateTime(dt: LocalDateTime): String {
        val day = dt.date.dayOfMonth.toString().padStart(2, '0')
        val month = dt.date.monthNumber.toString().padStart(2, '0')
        val year = dt.date.year
        val hour = dt.hour.toString().padStart(2, '0')
        val minute = dt.minute.toString().padStart(2, '0')
        val second = dt.second.toString().padStart(2, '0')
        return "$day/$month/$year $hour:$minute:$second"
    }
}
