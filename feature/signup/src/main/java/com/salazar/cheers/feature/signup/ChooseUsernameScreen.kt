package com.salazar.cheers.feature.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent
import com.salazar.cheers.core.ui.ui.AppBar
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.util.Utils.validateUsername

@Composable
fun ChooseUsernameScreen(
    username: String,
    isUsernameAvailable: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onClearUsername: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Sign Up",
                center = true,
                backNavigation = true,
                onNavigateBack = onBackPressed,
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .imePadding()
                .padding(16.dp),
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
                isUsernameAvailable = isUsernameAvailable,
                errorMessage = errorMessage,
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
}

@Composable
fun UsernameTextField(
    enabled: Boolean,
    username: String,
    errorMessage: String?,
    isUsernameAvailable: Boolean,
    onUsernameChanged: (String) -> Unit,
    onClearUsername: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onSearch = {
        }),
        placeholder = { Text("Username") },
        trailingIcon = {
            if (isUsernameAvailable)
                Icon(imageVector = Icons.Default.Check, null)
            else if (username.isNotEmpty())
                IconButton(onClick = onClearUsername) {
                    Icon(imageVector = Icons.Default.Close, null)
                }
        },
        isError = username.isNotEmpty() && !username.validateUsername(),
        enabled = enabled,
    )
    if (errorMessage?.isNotEmpty() == true)
        Text(
            text = errorMessage,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
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
            isUsernameAvailable = true,
            onClearUsername = {},
            onUsernameChanged = {},
            onNextClicked = {},
            onBackPressed = {},
        )
    }
}