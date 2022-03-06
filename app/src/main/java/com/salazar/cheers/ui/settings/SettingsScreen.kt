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
    navigateToAddPaymentMethod: () -> Unit,
    navigateToPaymentHistory: () -> Unit,
    navigateToBecomeVip: () -> Unit,
    navigateToRecharge: () -> Unit,
    onBackPressed: () -> Unit,
) {

    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Settings") },
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AccountSection(
                navigateToBecomeVip = navigateToBecomeVip,
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSection(
                navigateToTheme = navigateToTheme,
                navigateToNotifications = navigateToNotifications,
                navigateToLanguage = navigateToLanguage,
                navigateToAddPaymentMethod = navigateToAddPaymentMethod,
                navigateToPaymentHistory = navigateToPaymentHistory,
                navigateToRecharge = navigateToRecharge,
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
fun AccountSection(
    navigateToBecomeVip: () -> Unit,
) {
    Column() {
        SettingTitle(title = "Account")
        SettingItem("Become VIP", Icons.Outlined.WorkspacePremium, navigateToBecomeVip)
    }
}
@Composable
fun SettingsSection(
    navigateToTheme: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToAddPaymentMethod: () -> Unit,
    navigateToLanguage: () -> Unit,
    navigateToPaymentHistory: () -> Unit,
    navigateToRecharge: () -> Unit,
) {
    Column() {
        SettingTitle(title = "Settings")
        SettingItem("Notifications and Sounds", Icons.Outlined.Notifications, navigateToNotifications)
        SettingItem("Chat Settings", Icons.Outlined.ChatBubbleOutline, {})
        SettingItem("Devices", Icons.Outlined.Computer, {})
        SettingItem("Payment Methods", Icons.Outlined.CreditCard, navigateToAddPaymentMethod)
        SettingItem("Recharge coins", Icons.Outlined.CreditCard, navigateToRecharge)
        SettingItem("Payment History", Icons.Outlined.CreditCard, navigateToPaymentHistory)
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
