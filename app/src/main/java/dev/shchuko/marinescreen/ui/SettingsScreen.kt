package dev.shchuko.marinescreen.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shchuko.marinescreen.R
import dev.shchuko.marinescreen.domain.model.WeatherStationSettings

private enum class PasswordState(
    val alwaysShowFieldValue: Boolean,
    val displayedFieldValue: @Composable (passwordState: String, passwordEnterIsInProgress: Boolean) -> String,
    val hideRevealButtonActive: Boolean,
) {
    NORMAL(
        alwaysShowFieldValue = false,
        displayedFieldValue = { passwordStateValue, _ -> passwordStateValue },
        hideRevealButtonActive = true,
    ),
    REUSE_PASSWORD_FROM_SETTINGS(
        alwaysShowFieldValue = true,
        displayedFieldValue = { _, passwordEnterIsInProgress ->
            if (passwordEnterIsInProgress) {
                ""
            } else {
                stringResource(R.string.password_value_hidden)
            }
        },
        hideRevealButtonActive = false,
    ),
    ;
}

@Composable
@Preview(
    device = "id:tv_1080p",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_TELEVISION,
)
fun SettingsScreen(
    stationSettings: WeatherStationSettings = WeatherStationSettings.NOT_SET,
    onSaveClick: (WeatherStationSettings) -> Unit = {},
    onTestConnection: (uid: String, password: String) -> Unit = { _, _ -> },
    onBackConfirmed: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val backButtonFocusRequester = remember { FocusRequester() }
    val revealPasswordButtonFocusRequester = remember { FocusRequester() }

    var displayName by remember { mutableStateOf(stationSettings.displayName) }
    var stationUid by remember { mutableStateOf(stationSettings.stationUid) }

    /*
    1. old password not set
        * show empty field
        * reveal button active
    2. old password was set, field value edit is not in progress
        * show <password hidden> placeholder instead of password
        * reveal button inactive
    3. old password was set, field value edit is not in progress, nothing was entered
        * show empty field
        * reveal button inactive
    4. old password was set, value was entered
        * show password or its hidden representation *** (depends on the hide/reveal button press)
        * reveal button active
    flags:
     */

    var password by remember { mutableStateOf("") }
    var passwordState by remember {
        mutableStateOf(
            if (stationSettings.stationPassword.isNotEmpty()) PasswordState.REUSE_PASSWORD_FROM_SETTINGS
            else PasswordState.NORMAL
        )
    }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordEnterInProgress by remember { mutableStateOf(false) }
    var passwordModified by remember { mutableStateOf(false) }

    val effectivePasswordValue = when (passwordState) {
        PasswordState.NORMAL -> password
        PasswordState.REUSE_PASSWORD_FROM_SETTINGS -> stationSettings.stationPassword
    }
    val stationUidSet = stationUid.isNotEmpty()
    val passwordSet = effectivePasswordValue.isNotEmpty()
    val requiredSettingsSet = passwordSet && stationUidSet

    val settingsModified =
        displayName != stationSettings.displayName
                || stationUid != stationSettings.stationUid
                || passwordModified

    var showExitConfirmation by remember { mutableStateOf(false) }
    var alertDialogFocusRequesterReady by remember { mutableStateOf(false) }
    val alertDialogFocusRequester = remember { FocusRequester() }

    BackHandler {
        if (settingsModified) {
            showExitConfirmation = true
        } else {
            onBackConfirmed()
        }
    }

    LaunchedEffect(alertDialogFocusRequesterReady) {
        if (alertDialogFocusRequesterReady) {
            alertDialogFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            TvFocusableTextButton(
                onClick = {
                    if (settingsModified) {
                        showExitConfirmation = true
                    } else {
                        onBackConfirmed()
                    }
                },
                modifier = Modifier.focusRequester(backButtonFocusRequester),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.button_back),
                )
            }

        }

        Spacer(Modifier.height(24.dp))

        EditableOutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            label = { Text(stringResource(R.string.display_name_label)) },
            placeholder = {
                val defaultDisplayName = stringResource(R.string.display_name_default)
                Text(stringResource(R.string.display_name_placeholder, defaultDisplayName))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        EditableOutlinedTextField(
            value = stationUid,
            onValueChange = { stationUid = it },
            singleLine = true,
            isError = settingsModified && !stationUidSet,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            label = { Text(stringResource(R.string.station_uid_label)) },
            placeholder = { Text(stringResource(R.string.station_uid_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        EditableOutlinedTextField(
            value = passwordState.displayedFieldValue(password, passwordEnterInProgress),
            onValueChange = {
                passwordModified = true
                passwordState = PasswordState.NORMAL
                password = it
            },
            onReadOnlyChange = { readOnly -> passwordEnterInProgress = !readOnly },
            singleLine = true,
            isError = settingsModified && !passwordSet,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            label = { Text(stringResource(R.string.password_label)) },
            placeholder = {
                Text(stringResource(R.string.password_placeholder))
            },
            visualTransformation =
                if (passwordVisible || passwordState.alwaysShowFieldValue) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            modifier = Modifier
                .fillMaxWidth()
                .focusProperties {
                    if (passwordState.hideRevealButtonActive) {
                        down = revealPasswordButtonFocusRequester
                        next = revealPasswordButtonFocusRequester
                    }
                }
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TvFocusableTextButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.focusRequester(revealPasswordButtonFocusRequester),
                    enabled = passwordState.hideRevealButtonActive,
                    focusable = passwordState.hideRevealButtonActive,
                ) {
                    Text(
                        if (passwordVisible) stringResource(R.string.button_hide_password)
                        else stringResource(R.string.button_reveal_password)
                    )
                }

                Spacer(Modifier.width(12.dp))


                TvFocusableTextButton(
                    onClick = { onTestConnection(stationUid, password) },
                ) {
                    Text(stringResource(R.string.button_test_connection))
                }
            }

            TvFocusableButton(
                enabled = requiredSettingsSet && settingsModified,
                focusable = requiredSettingsSet && settingsModified,
                onClick = {
                    onSaveClick(
                        WeatherStationSettings(
                            displayName = displayName,
                            stationUid = stationUid,
                            stationPassword = effectivePasswordValue,
                        )
                    )

                    passwordModified = false
                    backButtonFocusRequester.requestFocus()
                },
            ) {
                Text(stringResource(R.string.button_save))
            }
        }
    }

    if (showExitConfirmation) {
        val onDialogClose: () -> Unit = {
            alertDialogFocusRequesterReady = false
            showExitConfirmation = false
        }

        AlertDialog(
            onDismissRequest = onDialogClose,
            title = { Text("Changes are not saved") },
            text = { Text("Settings are modified. Are you sure want to exit?") },
            dismissButton = {
                TvFocusableTextButton(
                    onClick = onDialogClose,
                    modifier = Modifier
                        .onGloballyPositioned { alertDialogFocusRequesterReady = true }
                        .focusRequester(alertDialogFocusRequester)
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TvFocusableTextButton(
                    onClick = {
                        onDialogClose()
                        onBackConfirmed()
                    }
                ) {
                    Text("Discard changes")
                }
            },
        )
    }
}

