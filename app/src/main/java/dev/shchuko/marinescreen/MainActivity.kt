package dev.shchuko.marinescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.shchuko.marinescreen.data.SettingsRepositoryImpl
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.ui.MainScreenPlaceholder
import dev.shchuko.marinescreen.ui.MainViewModel
import dev.shchuko.marinescreen.ui.TermsPopup
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepo: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsRepo = SettingsRepositoryImpl(applicationContext)

        setContent {
            val viewModel = remember { MainViewModel(settingsRepo) }
            val accepted by viewModel.termsAccepted.collectAsState()

            Box(modifier = Modifier.fillMaxSize()) {
                MainScreenPlaceholder(
                    onReject = {
                        viewModel.rejectTerms()
                    }
                )

                if (!accepted) {
                    TermsPopup(
                        onAccept = { viewModel.acceptTerms() },
                        onExit = {
                            viewModel.rejectTerms()
                            finishAffinity()
                            exitProcess(0)
                        }
                    )
                }
            }
        }
    }
}

