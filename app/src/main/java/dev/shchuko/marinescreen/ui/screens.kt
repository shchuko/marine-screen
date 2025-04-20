package dev.shchuko.marinescreen.ui

sealed class Screen(val route: String) {
    data object Weather : Screen("weather")
    data object Settings : Screen("settings")
}
