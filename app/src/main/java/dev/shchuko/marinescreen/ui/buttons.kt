package dev.shchuko.marinescreen.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer


@Composable
fun TvFocusableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    focusable: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val targetScale = if (enabled && isFocused) 1.1f else 1f
    val scale by animateFloatAsState(targetValue = targetScale, label = "FocusScale")

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .focusable(enabled = focusable, interactionSource = interactionSource),
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun TvFocusableTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    focusable: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val targetScale = if (enabled && isFocused) 1.1f else 1f
    val scale by animateFloatAsState(targetValue = targetScale, label = "FocusScale")

    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .focusable(enabled = focusable, interactionSource = interactionSource),
        interactionSource = interactionSource,
        content = content
    )
}

