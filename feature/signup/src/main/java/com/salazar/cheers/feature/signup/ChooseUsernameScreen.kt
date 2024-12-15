package com.salazar.cheers.feature.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.model.CheckUsernameResult
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent
import com.salazar.cheers.core.ui.ui.AppBar
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.util.Utils.validateUsername
import com.salazar.cheers.shared.util.Resource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseUsernameScreen(
    username: String,
    usernameAvailabilityState: Resource<CheckUsernameResult>,
    isLoading: Boolean,
    errorMessage: String?,
    onClearUsername: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(errorMessage) {
        if (errorMessage == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = errorMessage,
            withDismissAction = true,
        )
    }
    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Sign up",
                center = true,
                backNavigation = false,
                onNavigateBack = onBackPressed,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .imePadding()
                .imeNestedScroll(),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    MessageComponent(
                        image = R.drawable.fido,
                        title = "Choose username",
                        subtitle = "You can't change it later",
                    )
                    Spacer(Modifier.height(36.dp))
                    UsernameTextField(
                        enabled = !isLoading,
                        username = username,
                        focusRequester = focusRequester,
                        usernameAvailabilityState = usernameAvailabilityState,
                        onClearUsername = onClearUsername,
                        onUsernameChanged = onUsernameChanged,
                    )
                    Spacer(Modifier.height(8.dp))
                    ButtonWithLoading(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Create account",
                        isLoading = isLoading,
                        onClick = onNextClicked,
                        enabled = username.validateUsername(),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AsyncImage(
                        model = "https://www.gstatic.com/identity/boq/accountsettingsmobile/privacypolicy_icon_with_new_shield_48x48_3426417659bc0ba9f7866eead0c3e857.png",
                        contentDescription = "",
                    )
                    Text(
                        text = "Your login details are encrypted",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun UsernameTextField(
    enabled: Boolean,
    username: String,
    focusRequester: FocusRequester,
    usernameAvailabilityState: Resource<CheckUsernameResult>,
    onUsernameChanged: (String) -> Unit,
    onClearUsername: () -> Unit
) {
    val data = (usernameAvailabilityState as? Resource.Success)?.data
    val isError = data?.valid?.not() == true

    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
    }

    TextField(
        value = username,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .focusRequester(focusRequester = focusRequester),
        onValueChange = { onUsernameChanged(it) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(
            onSearch = {}
        ),
        placeholder = { Text("Username") },
        trailingIcon = {
            UsernameAvailabilityIconComponent(
                state = usernameAvailabilityState,
            )
        },
        supportingText = {
            Row {
                Text(
                    text = if (isError) data?.invalidReason.orEmpty() else "",
                    modifier = Modifier.clearAndSetSemantics {},
                )
//                Spacer(Modifier.weight(1f))
//                Text("Limit: ${state.text.length}/$charLimit")
            }
        },
        isError = isError,
        enabled = enabled,
    )
}

@ScreenPreviews
@Composable
fun ChooseUsernamePreview() {
    CheersPreview {
        ChooseUsernameScreen(
            username = "",
            errorMessage = null,
            isLoading = false,
            usernameAvailabilityState = Resource.Success(CheckUsernameResult(valid = true)),
            onClearUsername = {},
            onUsernameChanged = {},
            onNextClicked = {},
            onBackPressed = {},
        )
    }
}