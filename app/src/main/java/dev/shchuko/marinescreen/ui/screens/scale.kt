package dev.shchuko.marinescreen.ui.screens

import android.content.res.Configuration
import androidx.compose.ui.unit.dp

fun getWPaddingForScreenScale(screenScale: Float, configuration: Configuration) =
    ((100f - screenScale) / 100f * configuration.screenWidthDp).dp


fun getHPaddingForScreenScale(screenScale: Float, configuration: Configuration) =
    ((100f - screenScale) / 100f * configuration.screenHeightDp).dp

