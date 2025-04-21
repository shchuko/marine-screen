package dev.shchuko.marinescreen.data

import dev.shchuko.marinescreen.domain.model.StationSnapshot
import dev.shchuko.marinescreen.domain.model.WeatherSample
import dev.shchuko.marinescreen.domain.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock

class FakeStationRepository : StationRepository {
    companion object {
        private val FakeStationSnapshot = StationSnapshot(
            stationName = "...",
            current = WeatherSample(
                timestamp = Clock.System.now(),
                windSpeedKts = 10f,
                windSpeedMps = 5f,
                gustKts = 25f,
                gustMps = 17f,
                windDirectionDeg = 275,
                temperatureC = 25f,
                humidityPercent = 50
            ),
            historical = emptyList(),
            lastUpdatedAt = Clock.System.now()
        )
    }
    private val _station = MutableStateFlow<StationSnapshot?>(FakeStationSnapshot)
    override val station: StateFlow<StationSnapshot?> get() = _station

    override suspend fun refresh() {
    }
}
