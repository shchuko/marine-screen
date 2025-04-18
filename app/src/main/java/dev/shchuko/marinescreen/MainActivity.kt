package dev.shchuko.marinescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.shchuko.marinescreen.ui.MainScreenPlaceholder
import dev.shchuko.marinescreen.ui.MainViewModel
import dev.shchuko.marinescreen.ui.TermsPopup
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
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

