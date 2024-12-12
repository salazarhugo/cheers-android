package com.salazar.cheers.feature.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent
import com.salazar.cheers.core.ui.ui.AppBar
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.util.Utils.validateUsername
import com.salazar.cheers.shared.util.Resource

@Composable
fun ChooseUsernameScreen(
    username: String,
    usernameAvailabilityState: Resource<Boolean>,
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
                backNavigation = false,
                onNavigateBack = onBackPressed,
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
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
            Row(
                modifier = Modifier.padding(8.dp),
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
            ErrorMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                errorMessage = errorMessage,
            )
        }
    }
}

@Composable
fun UsernameTextField(
    enabled: Boolean,
    username: String,
    usernameAvailabilityState: Resource<Boolean>,
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
        isError = username.isNotEmpty() && !username.validateUsername(),
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
            usernameAvailabilityState = Resource.Success(true),
            onClearUsername = {},
            onUsernameChanged = {},
            onNextClicked = {},
            onBackPressed = {},
        )
    }
}