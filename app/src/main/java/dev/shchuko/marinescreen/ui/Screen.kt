package dev.shchuko.marinescreen.ui

sealed class Screen(val route: String) {
    data object Terms : Screen("terms")
    data object Weather : Screen("weather")
    data object Settings : Screen("settings")
}
