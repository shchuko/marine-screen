package dev.shchuko.marinescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.time.PreciseTimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.model.StationSnapshot
import dev.shchuko.marinescreen.domain.repository.StationRepository
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
    private val settingsRepository: SettingsRepository,
    private val stationRepository: StationRepository,
    private val timeProvider: PreciseTimeProvider,
) : ViewModel() {

    private val _time = MutableStateFlow("00/00/0000 00:00:00")
    val time: StateFlow<String> = _time

    val termsAccepted: StateFlow<Boolean> = settingsRepository.termsAcceptedFlow
    val stationSnapshot: StateFlow<StationSnapshot?> = stationRepository.station

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
