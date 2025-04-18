package dev.shchuko.marinescreen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreenPlaceholder(
    onReject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MarineScreen: Main Screen Placeholder",
            fontSize = 20.sp
        )

        Button(onClick = {
            onReject()
        }) {
            Text("Reset T&C")
        }
    }
}
