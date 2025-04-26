package dev.shchuko.marinescreen.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shchuko.marinescreen.R
import dev.shchuko.marinescreen.ui.tv.TvFocusableButton
import dev.shchuko.marinescreen.ui.tv.TvFocusableTextButton
import kotlin.math.min
import androidx.compose.material3.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dev.shchuko.marinescreen.ui.tv.handleDPadKeyEvents

@Composable
@Preview(
    device = "id:tv_1080p",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_TELEVISION,
)
fun ScreenScaleScreen(
    initialPaddingPercent: Double = 0.0,
    onSave: (paddingPercent: Double) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val backButtonFocusRequester = remember { FocusRequester() }
    var paddingPercent by remember { mutableDoubleStateOf(initialPaddingPercent) }
    val configuration = LocalConfiguration.current
    val minSideDp = min(configuration.screenWidthDp, configuration.screenHeightDp)
    val actualPaddingDp = (paddingPercent / 100f) * minSideDp

    BackHandler { onBack() }

    Box(modifier = Modifier.fillMaxSize()) {
        Background(actualPaddingDp)

        Surface(
            modifier = Modifier
                .padding(32.dp)
                .padding(horizontal = actualPaddingDp.dp, vertical = actualPaddingDp.dp)
                .wrapContentSize(Alignment.Center), // <- Center small Surface,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
//            shape = MaterialTheme.shapes.medium, // Optional: add slight rounding
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp) // internal padding inside Surface
                    .wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Screen scale",
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(Modifier.width(24.dp))
                    TvFocusableTextButton(
                        onClick = { onSave(paddingPercent) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }


                Text(
                    text = "Current screen scale ${"%.1f".format((100 - paddingPercent))}%",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TvFocusableButton(
                        onClick = {
                            paddingPercent = (paddingPercent + 0.5).coerceAtMost(5.0)
                        },
                    ) {
                        Text("Zoom in")
                    }

                    TvFocusableButton(
                        onClick = {
                            paddingPercent = (paddingPercent - 0.5).coerceAtLeast(0.0)
                        },
                    ) {
                        Text("Zoom out")
                    }

                    TvFocusableButton(
                        onClick = { onSave(paddingPercent) },
                    ) {
                        Text("Save")
                    }
                }


            }
        }

    }
}

@Composable
private fun Background(actualPaddingDp: Double) {
    // Background canvas
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = actualPaddingDp.dp, vertical = actualPaddingDp.dp)
    ) {
        val stripeWidth = size.width / 7
        val colors = listOf(
            Color.LightGray,
            Color.Yellow,
            Color.Cyan,
            Color.Green,
            Color.Magenta,
            Color.Red,
            Color.Blue,
        )
        colors.forEachIndexed { index, color ->
            drawRect(
                color = color,
                topLeft = Offset(x = index * stripeWidth, y = 0f),
                size = Size(stripeWidth, size.height)
            )
        }
        // Corner markers
        val cornerSize = size.minDimension / 15
        val markerColor = Color.Black

        drawRect(markerColor, topLeft = Offset(0f, 0f), size = Size(cornerSize, 10f))
        drawRect(markerColor, topLeft = Offset(0f, 0f), size = Size(10f, cornerSize))
        drawRect(
            markerColor,
            topLeft = Offset(size.width - cornerSize, 0f),
            size = Size(cornerSize, 10f)
        )
        drawRect(markerColor, topLeft = Offset(size.width - 10f, 0f), size = Size(10f, cornerSize))
        drawRect(markerColor, topLeft = Offset(0f, size.height - 10f), size = Size(cornerSize, 10f))
        drawRect(
            markerColor,
            topLeft = Offset(0f, size.height - cornerSize),
            size = Size(10f, cornerSize)
        )
        drawRect(
            markerColor,
            topLeft = Offset(size.width - cornerSize, size.height - 10f),
            size = Size(cornerSize, 10f)
        )
        drawRect(
            markerColor,
            topLeft = Offset(size.width - 10f, size.height - cornerSize),
            size = Size(10f, cornerSize)
        )
    }
}
