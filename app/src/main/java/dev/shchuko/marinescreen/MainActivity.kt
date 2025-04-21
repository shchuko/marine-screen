package dev.shchuko.marinescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.shchuko.marinescreen.ui.MainViewModel
import dev.shchuko.marinescreen.ui.Screen
import dev.shchuko.marinescreen.ui.screens.SettingsScreen
import dev.shchuko.marinescreen.ui.screens.TermsPopupContent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val viewModel = hiltViewModel<MainViewModel>()
            val termsAccepted: Boolean by viewModel.termsAccepted.collectAsState()
            val stationSnapshot by viewModel.stationSnapshot.collectAsState()
            val time by viewModel.time.collectAsState()
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
                    stationSnapshot?.let { snapshot ->
                        WeatherScreenStub(
                            time = time,
                            stationName = snapshot.stationName,
                            wind = snapshot.current?.windSpeedKts,
                            windDir = snapshot.current?.windDirectionDeg,
                            temperatureC = snapshot.current?.temperatureC,
                            temperatureF = snapshot.current?.temperatureF,
                            humidity = snapshot.current?.humidityPercent,
                            updatedAt = snapshot.lastUpdatedAt,
                            onSettingsClick = { navController.navigate(Screen.Settings.route) }
                        )
                    } ?: Box(Modifier.fillMaxSize()) {
                        Text("Loading...")
                    }
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

@Composable
fun WeatherScreenStub(
    time: String,
    stationName: String,
    wind: Float?,
    windDir: Int?,
    temperatureC: Float?,
    temperatureF: Float?,
    humidity: Int?,
    updatedAt: Instant,
    onSettingsClick: () -> Unit = {},
    onWarningClick: () -> Unit = {}
) {
    val updatedAgo = remember(updatedAt) { updatedAt.formatAgo() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(24.dp)
    ) {
        // Top right: time + station name + gear
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(time, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(stationName, color = Color.White, fontSize = 16.sp)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        // Center wind data
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Wind ${wind?.toInt() ?: "..."} kts",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Dir ${windDir ?: "..."}°", color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Temp ${temperatureC?.let { "+${it.toInt()}°C" } ?: "..."} / ${temperatureF?.let { "+${it.toInt()}°F" } ?: "..."}",
                color = Color.White,
                fontSize = 18.sp)
            Text("Humidity ${humidity?.let { "$it%" } ?: "..."}",
                color = Color.White,
                fontSize = 18.sp)
        }

        // Bottom left: source
        Text(
            text = "github.com/shchuko/marine-screen",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )

        // Bottom right: updated + warning icon
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onWarningClick() },
                tint = Color.Yellow
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Updated $updatedAgo",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

fun Instant.formatAgo(): String {
    val mins = Clock.System.now().minus(this).inWholeMinutes
    return when {
        mins < 1 -> "just now"
        mins == 1L -> "1 min ago"
        mins > 60 -> "${mins / 60} hours ago"
        else -> "$mins min ago"
    }
}