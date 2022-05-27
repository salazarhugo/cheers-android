package com.salazar.cheers.ui.auth.signin.username

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
import com.salazar.cheers.components.share.AppBar
import com.salazar.cheers.components.share.ButtonWithLoading
import com.salazar.cheers.ui.main.add.TopAppBar

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
                title = "",
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
                .padding(16.dp),
        ) {
            Text(
                text = "Choose username",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(36.dp))
            UsernameTextField(
                username = username,
                isUsernameAvailable = isUsernameAvailable,
                errorMessage = errorMessage,
                onClearUsername = onClearUsername,
                onUsernameChanged = onUsernameChanged,
            )
            Spacer(Modifier.height(8.dp))
            ButtonWithLoading(
                text = "Next",
                isLoading = isLoading,
                onClick = onNextClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun UsernameTextField(
    username: String,
    errorMessage: String?,
    isUsernameAvailable: Boolean,
    onUsernameChanged: (String) -> Unit,
    onClearUsername: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = username,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
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
                Icon(imageVector = Icons.Default.Check, "")
            else if (username.isNotEmpty())
                IconButton(onClick = onClearUsername) {
                    Icon(imageVector = Icons.Default.Close, "")
                }
        },
        isError = !isUsernameAvailable,
    )
    if (errorMessage?.isNotEmpty() == true)
        Text(errorMessage, color = MaterialTheme.colorScheme.error)
}
