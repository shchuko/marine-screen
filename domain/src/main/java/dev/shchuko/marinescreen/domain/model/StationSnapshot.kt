package dev.shchuko.marinescreen.domain.model

import kotlinx.datetime.Instant

data class StationSnapshot(
    val stationName: String,
    val current: WeatherSample?,
    val historical: List<WeatherSample>,
    val lastUpdatedAt: Instant
)

data class WeatherSample(
    val timestamp: Instant,
    val windSpeedKts: Float?,
    val windSpeedMps: Float?,
    val gustKts: Float?,
    val gustMps: Float?,
    val windDirectionDeg: Int?,
    val temperatureC: Float?,
    val temperatureF: Float? = temperatureC?.let { it * 9 / 5 + 32 },
    val humidityPercent: Int?,
)
