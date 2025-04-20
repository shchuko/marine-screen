package dev.shchuko.marinescreen.ui.tv

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun EditableOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusProperties: (FocusProperties.() -> Unit)? = null,
    isError: Boolean = false,
    onReadOnlyChange: (Boolean) -> Unit = {}
) {
    var isReadOnly by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        snapshotFlow { isReadOnly }
            .collect { value ->
                onReadOnlyChange(value)
            }
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                isReadOnly = false
            }
            if (interaction is FocusInteraction.Unfocus) {
                isReadOnly = true
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (!isReadOnly) onValueChange(it)
        },
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .then(if (focusProperties != null) Modifier.focusProperties(focusProperties) else Modifier)
            .handleDPadKeyEvents(onEnter = { isReadOnly = false }),
        label = label,
        isError = isError,
        placeholder = placeholder,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        readOnly = isReadOnly,
        interactionSource = interactionSource
    )
}
