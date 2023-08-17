package com.salazar.cheers.feature.settings.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun SecurityScreen(
    uiState: SecurityUiState,
    onBackPressed: () -> Unit,
    onUnlink: (String) -> Unit,
    onLink: (String) -> Unit,
    onAddPassword: (Boolean) -> Unit,
    onPasscodeClick: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Security") },
    ) {
        Column(
            modifier = Modifier
                .padding(it),
        ) {
//            val user = uiState.firebaseUser!!
//            val verified = user.isEmailVerified

            val signInMethods = uiState.signInMethods

            SettingTitle(title = "Sign in methods")

//            if (user.isAnonymous)
//                SignInMethodItem(method = "Anonymous")
            val hasEmailPassword =
                true
//                signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)
            val hasEmailLink =
                true
//                signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)
            val hasGoogle =
                true
//                signInMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)

            val passcodeLockEnabled = true
            SettingItem(
                title = "Passcode Lock",
                icon = Icons.Outlined.Lock,
                onClick = onPasscodeClick,
                trailingContent = {
                    val text = if (passcodeLockEnabled) "On" else "Off"
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

            val text = if (hasEmailPassword) "Update Password" else "Create Password"

            ErrorMessage(
                errorMessage = uiState.errorMessage,
                paddingValues = PaddingValues()
            )
        }
    }
}

@Composable
fun SignInMethodItem(
    method: String,
    icon: ImageVector,
    linked: Boolean,
    unlinkable: Boolean = false,
    onClick: () -> Unit = {},
    onUnlink: () -> Unit,
    onLink: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = method,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (linked) {
                if (unlinkable) {
                    Text(
                        modifier = Modifier.clickable { onUnlink() },
                        text = "Unlink",
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = com.salazar.cheers.core.share.ui.GreenGoogle,
                )
            } else {
                TextButton(
                    onClick = { onLink() }
                ) {
                    Text(
                        text = "Link",
                    )
                }
            }
        }
    }
}