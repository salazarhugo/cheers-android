package com.salazar.cheers.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.BuildConfig
import com.salazar.cheers.components.items.SettingItem
import com.salazar.cheers.components.items.SettingTitle
import com.salazar.cheers.components.share.Toolbar

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSignOut: () -> Unit,
    navigateToTheme: () -> Unit,
    navigateToLanguage: () -> Unit,
    navigateToNotifications: () -> Unit,
    onBackPressed: () -> Unit,
) {

    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Settings") },
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SettingsSection(
                navigateToTheme = navigateToTheme,
                navigateToNotifications = navigateToNotifications,
                navigateToLanguage = navigateToLanguage,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HelpSection()
            Spacer(modifier = Modifier.height(16.dp))
            LoginsSection(onSignOut = onSignOut)
            Spacer(modifier = Modifier.height(16.dp))
            VersionSection()
        }
    }

}

@Composable
fun VersionSection() {
    Text(
        text = "Cheers v.${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun LoginsSection(
    onSignOut: () -> Unit,
) {
    Column() {
        SettingTitle(title = "Logins")
        SignOutButton(onSignOut = onSignOut)
        DeleteAccountButton()
    }
}

@Composable
fun HelpSection() {
    Column() {
        SettingTitle(title = "Help")
        SettingItem("Ask a Question", Icons.Outlined.QuestionAnswer, {})
        SettingItem("Privacy Policy", Icons.Outlined.Policy, {})
    }
}

@Composable
fun SettingsSection(
    navigateToTheme: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToLanguage: () -> Unit,
) {
    Column() {
        SettingTitle(title = "Settings")
        SettingItem("Notifications and Sounds", Icons.Outlined.Notifications, navigateToNotifications)
        SettingItem("Chat Settings", Icons.Outlined.ChatBubbleOutline, {})
        SettingItem("Devices", Icons.Outlined.Computer, {})
        SettingItem("Language", Icons.Outlined.Language, navigateToLanguage)
        SettingItem("Theme", Icons.Outlined.Palette, navigateToTheme)
        SettingItem("About", Icons.Outlined.Info, {})
    }
}

@Composable
fun DeleteAccountButton() {
    TextButton(
        onClick = {},
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = "Delete account",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
fun SignOutButton(onSignOut: () -> Unit) {
    TextButton(
        onClick = onSignOut,
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = "Log Out",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Start,
        )
    }
}
