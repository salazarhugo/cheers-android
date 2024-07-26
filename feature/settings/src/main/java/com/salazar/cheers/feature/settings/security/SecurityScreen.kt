package com.salazar.cheers.feature.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Credential
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun SecurityScreen(
    uiState: SecurityUiState,
    onBackPressed: () -> Unit = {},
    onUnlink: (String) -> Unit = {},
    onLink: (String) -> Unit = {},
    onAddPassword: (Boolean) -> Unit = {},
    onPasscodeClick: () -> Unit = {},
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Security") },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it),
        ) {
            val passcodeEnabled = uiState.passcodeEnabled

            passkeys(
                credentials = uiState.credentials,
            )

            signInMethods(
                passcodeEnabled = passcodeEnabled,
                onPasscodeClick = onPasscodeClick,
                onAddPassword = onAddPassword,
            )

            item {
                ErrorMessage(
                    errorMessage = uiState.errorMessage,
                    paddingValues = PaddingValues()
                )
            }
        }
    }
}

private fun LazyListScope.passkeys(
    credentials: List<Credential>,
) {
    if (credentials.isEmpty()) return

    item {
        SettingTitle(title = "Passkeys")
    }

    items(
        items = credentials,
    ) { passkey ->
        PasskeyItem(
            credential = passkey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

private fun LazyListScope.signInMethods(
    passcodeEnabled: Boolean,
    onPasscodeClick: () -> Unit,
    onAddPassword: (Boolean) -> Unit,
) {
    item {
        SettingTitle(title = "Sign in methods")

        val hasEmailPassword = true
        val hasEmailLink = true
        val hasGoogle = true

        SettingItem(
            title = "Passcode Lock",
            icon = Icons.Outlined.Lock,
            onClick = onPasscodeClick,
            trailingContent = {
                val text = if (passcodeEnabled) "On" else "Off"
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        )

        SignInMethodItem(
            method = "Password",
            icon = Icons.Default.Password,
            onUnlink = {},
            linked = hasEmailPassword,
            onClick = { onAddPassword(hasEmailPassword) },
            onLink = { onAddPassword(hasEmailPassword) },
        )

        SignInMethodItem(
            method = "Email-Link",
            icon = Icons.Outlined.Email,
            onUnlink = {
//                    onUnlink(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)
            },
            linked = hasEmailLink,
        )

        SignInMethodItem(
            method = "Google",
            icon = Icons.Outlined.Gamepad,
            onUnlink = {
//                    onUnlink(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
            },
            onLink = {
//                    onLink(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
            },
            linked = hasGoogle,
            unlinkable = true,
        )
    }
}

@ScreenPreviews
@Composable
private fun SecurityScreenPreview() {
    CheersPreview {
        SecurityScreen(
            uiState = SecurityUiState(),
            onBackPressed = { /*TODO*/ },
        )
    }
}