package dev.shchuko.marinescreen.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shchuko.marinescreen.R
import dev.shchuko.marinescreen.ui.tv.TvFocusableButton
import dev.shchuko.marinescreen.ui.tv.TvFocusableTextButton
import kotlin.math.min

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
    var paddingPercent by remember { mutableDoubleStateOf(initialPaddingPercent) }
    val configuration = LocalConfiguration.current
    val minSideDp = min(configuration.screenWidthDp, configuration.screenHeightDp)
    val actualPaddingDp = (paddingPercent / 100f) * minSideDp

    BackHandler { onBack() }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = actualPaddingDp.dp, vertical = actualPaddingDp.dp),
    ) {
        Background()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.wrapContentSize(),

                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.wrapContentSize().widthIn(400.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.screen_scale_title),
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            fontWeight = FontWeight.Bold,
                        )

                        TvFocusableTextButton(
                            onClick = { onSave(paddingPercent) },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.button_back),
                            )
                        }
                    }


                    Text(
                        text = stringResource(R.string.screen_scale_label) + "%.1f".format((100 - paddingPercent)),
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
                            Text(stringResource(R.string.button_zoom_in))
                        }

                        TvFocusableButton(
                            onClick = {
                                paddingPercent = (paddingPercent - 0.5).coerceAtLeast(0.0)
                            },
                        ) {
                            Text(stringResource(R.string.button_zoom_out))
                        }

                        TvFocusableButton(
                            onClick = { onSave(paddingPercent) },
                        ) {
                            Text(stringResource(R.string.button_save))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Background() {
    Canvas(modifier = Modifier.fillMaxSize()) {
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
        val cornerSize = size.minDimension / 15
        val markerColor = Color.Black
        val width = 10f
        drawRect(markerColor, topLeft = Offset(0f, 0f), size = Size(cornerSize, width))
        drawRect(markerColor, topLeft = Offset(0f, 0f), size = Size(width, cornerSize))
        drawRect(markerColor, topLeft = Offset(size.width - cornerSize, 0f), size = Size(cornerSize, width))
        drawRect(markerColor, topLeft = Offset(size.width - width, 0f), size = Size(width, cornerSize))
        drawRect(markerColor, topLeft = Offset(0f, size.height - width), size = Size(cornerSize, width))
        drawRect(markerColor, topLeft = Offset(0f, size.height - cornerSize), size = Size(width, cornerSize))
        drawRect(markerColor, topLeft = Offset(size.width - cornerSize, size.height - width), size = Size(cornerSize, width))
        drawRect(markerColor, topLeft = Offset(size.width - width, size.height - cornerSize), size = Size(width, cornerSize))
    }
}
