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
import com.salazar.cheers.compose.items.SettingItem
import com.salazar.cheers.compose.items.SettingTitle
import com.salazar.cheers.compose.share.ErrorMessage
import com.salazar.cheers.compose.share.Toolbar

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSettingsUIAction: (SettingsUIAction) -> Unit,
    onSignOut: () -> Unit,
    navigateToBecomeVip: () -> Unit,
    onBackPressed: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Settings") },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            AccountSection(navigateToBecomeVip = navigateToBecomeVip)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSection(onSettingsUIAction = onSettingsUIAction)
            Spacer(modifier = Modifier.height(16.dp))
            HelpSection(onSettingsUIAction = onSettingsUIAction)
            Spacer(modifier = Modifier.height(16.dp))
            LoginsSection(onSignOut = onSignOut, onDeleteAccount = onDeleteAccount)
            ErrorMessage(errorMessage = uiState.errorMessage, paddingValues = PaddingValues(16.dp))
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun LoginsSection(
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column {
        SettingTitle(title = "Logins")
        SignOutButton(onSignOut = onSignOut)
        RedButton(text = "Delete Account", onClick = onDeleteAccount)
    }
}

@Composable
fun HelpSection(
    onSettingsUIAction: (SettingsUIAction) -> Unit,
) {
    Column {
        SettingTitle(title = "Help")
        SettingItem("Ask a Question", Icons.Outlined.QuestionAnswer, {})
        SettingItem("Privacy Policy", Icons.Outlined.Policy) {
            onSettingsUIAction(SettingsUIAction.OnPrivacyPolicyClick)
        }
        SettingItem("Terms of Use", Icons.Outlined.Policy) {
            onSettingsUIAction(SettingsUIAction.OnTermsOfUseClick)
        }
    }
}

@Composable
fun AccountSection(
    navigateToBecomeVip: () -> Unit,
) {
    Column {
        SettingTitle(title = "Account")
        SettingItem("Become VIP", Icons.Outlined.WorkspacePremium, navigateToBecomeVip)
    }
}

@Composable
fun SettingsSection(
    onSettingsUIAction: (SettingsUIAction) -> Unit,
) {
    Column {
        SettingTitle(title = "Settings")
        SettingItem("Notifications and Sounds", Icons.Outlined.Notifications) {
            onSettingsUIAction(SettingsUIAction.OnNotificationsClick)
        }
        SettingItem("Chat Settings", Icons.Outlined.ChatBubbleOutline) {
            onSettingsUIAction(SettingsUIAction.OnNotificationsClick)
        }
        SettingItem("Security", Icons.Outlined.Security) {
            onSettingsUIAction(SettingsUIAction.OnSecurityClick)
        }
        SettingItem("Devices", Icons.Outlined.Computer, {})
//        SettingItem("Payment Methods", Icons.Outlined.CreditCard) {
//            onSettingsUIAction(SettingsUIAction.OnAddPaymentClick)
//        }
        SettingItem("Recharge coins", Icons.Outlined.CreditCard) {
            onSettingsUIAction(SettingsUIAction.OnRechargeClick)
        }
//        SettingItem("Payment History", Icons.Outlined.CreditCard) {
//            onSettingsUIAction(SettingsUIAction.OnPaymentHistoryClick)
//        }
        SettingItem("Language", Icons.Outlined.Language) {
            onSettingsUIAction(SettingsUIAction.OnLanguageClick)
        }
        SettingItem("Theme", Icons.Outlined.Palette) {
            onSettingsUIAction(SettingsUIAction.OnThemeClick)
        }
        SettingItem("About", Icons.Outlined.Info, {})
    }
}

@Composable
fun RedButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Start,
        )
    }
}
