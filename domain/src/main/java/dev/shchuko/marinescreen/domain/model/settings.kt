package dev.shchuko.marinescreen.domain.model

data class WindGuruSettings(
    val stationName: String,
    val windGuruUid: String,
    val windGuruPassword: String,
) {
    init {
        val settingsValid =
            windGuruUid.isNotEmpty() && windGuruPassword.isNotEmpty()
                || stationName.isEmpty() && windGuruUid.isEmpty() && windGuruPassword.isEmpty()

        if (!settingsValid) {
            throw InvalidSettingsException()
        }
    }

    companion object {
        val NOT_SET = WindGuruSettings(
            stationName = "",
            windGuruUid = "",
            windGuruPassword = "",
        )
    }
}

class InvalidSettingsException : Exception()