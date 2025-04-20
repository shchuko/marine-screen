package dev.shchuko.marinescreen.domain.model

data class WeatherStationSettings(
    val displayName: String,
    val stationUid: String,
    val stationPassword: String,
) {
    init {
        val settingsValid =
            stationUid.isNotEmpty() && stationPassword.isNotEmpty()
                || displayName.isEmpty() && stationUid.isEmpty() && stationPassword.isEmpty()

        if (!settingsValid) {
            throw InvalidSettingsException()
        }
    }

    companion object {
        val NOT_SET = WeatherStationSettings(
            displayName = "",
            stationUid = "",
            stationPassword = "",
        )
    }
}

class InvalidSettingsException() : Exception()