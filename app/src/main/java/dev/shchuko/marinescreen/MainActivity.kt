package dev.shchuko.marinescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.shchuko.marinescreen.ui.MainViewModel
import dev.shchuko.marinescreen.ui.Screen
import dev.shchuko.marinescreen.ui.screens.SettingsScreen
import dev.shchuko.marinescreen.ui.screens.TermsPopupContent
import dev.shchuko.marinescreen.ui.screens.WeatherScreen
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val viewModel = hiltViewModel<MainViewModel>()
            val termsAccepted: Boolean by viewModel.termsAccepted.collectAsState()
            val stationSnapshot by viewModel.stationMeasurements.collectAsState()
            val time by viewModel.time.collectAsState()
            val firstNtpSyncDone by viewModel.firstNtpSyncDone.collectAsState()
            val settings by viewModel.stationSettings.collectAsState()

            NavHost(
                navController = navController,
                startDestination = if (termsAccepted) {
                    Screen.Weather.route
                } else {
                    Screen.Terms.route
                }
            ) {
                composable(Screen.Terms.route) {
                    TermsPopupContent(
                        onAccept = {
                            viewModel.acceptTerms()
                            navController.navigate(Screen.Weather.route)
                        },
                        onExit = {
                            viewModel.rejectTerms()
                            finishAffinity()
                            exitProcess(0)
                        }
                    )
                }

                composable(Screen.Weather.route) {
                    WeatherScreen(
                        time = time,
                        firstNtpSyncDone = firstNtpSyncDone,
                        measurements = stationSnapshot,
                        onSettingsClick = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        stationSettings = settings,
                        onSaveClick = {
                            viewModel.saveStationSettings(it)
                        },
                        onTestConnection = { uid, password -> /* TODO */ },
                        onBackConfirmed = { navController.popBackStack() }
                    )
                }
            }
        }

    }
}