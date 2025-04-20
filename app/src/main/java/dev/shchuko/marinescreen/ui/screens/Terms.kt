package dev.shchuko.marinescreen.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shchuko.marinescreen.R
import dev.shchuko.marinescreen.ui.tv.TvFocusableButton
import dev.shchuko.marinescreen.ui.tv.TvFocusableTextButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview(
    device = "id:tv_1080p",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_TELEVISION,
)
fun TermsPopupContent(
    onAccept: () -> Unit = {},
    onExit: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val scrolledToEnd by remember { derivedStateOf { scrollState.value >= scrollState.maxValue } }
    val prevButtonEnabled by remember { derivedStateOf { scrollState.value > 0 } }
    val termsText by rememberPrivacyPolicyText()
    val focusRequester = remember { FocusRequester() }
    var isReadyToRequestFocus by remember { mutableStateOf(false) }
    var termsTextHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(isReadyToRequestFocus) { focusRequester.requestFocus() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.terms_and_conditions_title),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.terms_and_conditions_sub_title),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        termsTextHeight = it.size.height
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)
                    .verticalScroll(scrollState)
            ) {
                SelectionContainer(
                    modifier = Modifier.focusable(false),
                ) {
                    Text(
                        text = termsText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.focusable(false)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TvFocusableTextButton(
                    onClick = onExit,
                    modifier = Modifier.focusProperties {
                        left = FocusRequester.Cancel
                        up = FocusRequester.Cancel
                        down = FocusRequester.Cancel
                    }
                ) {
                    Text(stringResource(R.string.button_exit))
                }

                Spacer(modifier = Modifier.width(16.dp))

                TvFocusableTextButton(
                    onClick = {
                        val scrollAmount = termsTextHeight * 0.85f
                        scope.launch {
                            scrollState.animateScrollBy(-scrollAmount)
                        }
                    },
                    enabled = prevButtonEnabled,
                    focusable = prevButtonEnabled,
                    modifier = Modifier.focusProperties {
                        up = FocusRequester.Cancel
                        down = FocusRequester.Cancel
                    }
                ) {
                    Text(stringResource(R.string.button_prev))
                }

                Spacer(modifier = Modifier.width(16.dp))

                TvFocusableButton(
                    onClick = {
                        if (scrolledToEnd) {
                            onAccept()
                        } else {
                            val scrollAmount = termsTextHeight * 0.85f
                            scope.launch {
                                scrollState.animateScrollBy(scrollAmount)
                            }
                        }
                    },
                    modifier = Modifier
                        .onGloballyPositioned { isReadyToRequestFocus = true }
                        .focusRequester(focusRequester)
                        .focusProperties {
                            right = FocusRequester.Cancel
                            up = FocusRequester.Cancel
                            down = FocusRequester.Cancel
                        }
                        .widthIn(min = 90.dp)
                ) {
                    Text(
                        if (scrolledToEnd) stringResource(R.string.button_agree)
                        else stringResource(R.string.button_next)
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberPrivacyPolicyText(): State<String> {
    val context = LocalContext.current
    return produceState(initialValue = "", context) {
        value = withContext(Dispatchers.IO) {
            context.resources.openRawResource(R.raw.privacy_policy_and_terms)
                .bufferedReader()
                .use { it.readText() }
        }
    }
}
