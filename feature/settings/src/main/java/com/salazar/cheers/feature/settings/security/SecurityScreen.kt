package com.salazar.cheers.feature.settings.security

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.settings.security.passkeys.CreatePasskeyCardComponent

@Composable
fun SecurityScreen(
    uiState: SecurityUiState,
    onBackPressed: () -> Unit = {},
    onPasscodeClick: () -> Unit = {},
    onPasskeysClick: () -> Unit,
    onCreatePasskeyClick: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Security") },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it),
        ) {
            val passcodeEnabled = uiState.passcodeEnabled

            signInMethods(
                hasPasskeys = uiState.passkeys.isNotEmpty(),
                passcodeEnabled = passcodeEnabled,
                onPasscodeClick = onPasscodeClick,
                onPasskeysClick = onPasskeysClick,
                onCreatePasskeyClick = onCreatePasskeyClick,
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

private fun LazyListScope.signInMethods(
    hasPasskeys: Boolean,
    passcodeEnabled: Boolean,
    onPasscodeClick: () -> Unit,
    onPasskeysClick: () -> Unit,
    onCreatePasskeyClick: () -> Unit,
) {
    item {
        SettingTitle(title = "Sign in methods")

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

        if (hasPasskeys) {
            SignInMethodItem(
                method = "Passkeys",
                icon = Icons.Default.Key,
                onUnlink = {},
                linked = true,
                unlinkable = false,
                onClick = onPasskeysClick,
                onLink = {},
            )
        } else {
            CreatePasskeyCardComponent(
                modifier = Modifier.padding(16.dp),
                onClick = onCreatePasskeyClick,
            )
        }

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
            onPasskeysClick = {},
            onPasscodeClick = {},
            onCreatePasskeyClick = {}
        )
    }
}