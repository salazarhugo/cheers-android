package com.salazar.cheers.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.ui.ui.Toolbar

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
        topBar = {
            Toolbar(
                onBackPressed = onBackPressed,
                title = stringResource(id = R.string.settings),
            )
        },
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
    Text(text = "")
//    Text(
//        text = "${stringResource(id = R.string.app_name)} v.${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
//        style = MaterialTheme.typography.bodyMedium,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        textAlign = TextAlign.Center,
//    )
}

@Composable
fun LoginsSection(
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column {
        SettingTitle(
            title = stringResource(id = R.string.logins),
        )
        SignOutButton(
            onSignOut = onSignOut,
        )
        RedButton(
            stringResource(id = R.string.delete_account),
            onClick = onDeleteAccount,
        )
    }
}

@Composable
fun HelpSection(
    onSettingsUIAction: (SettingsUIAction) -> Unit,
) {
    Column {
        SettingTitle(
            title = stringResource(id = R.string.help),
        )
        SettingItem(
            title = stringResource(id = R.string.ask_a_question),
            icon = Icons.Outlined.QuestionAnswer,
        )
        SettingItem(
            title = stringResource(id = R.string.privacy_policy),
            icon = Icons.Outlined.Policy,
            onClick = { onSettingsUIAction(SettingsUIAction.OnPrivacyPolicyClick) },
        )
        SettingItem(
            title = stringResource(id = R.string.terms_of_use),
            icon = Icons.Outlined.Policy,
            onClick = { onSettingsUIAction(SettingsUIAction.OnTermsOfUseClick) },
        )
    }
}

@Composable
fun AccountSection(
    navigateToBecomeVip: () -> Unit,
) {
    Column {
        SettingTitle(
            title = stringResource(id = R.string.account),
        )
        SettingItem(
            title = "Become VIP",
            icon = Icons.Outlined.WorkspacePremium,
            onClick = navigateToBecomeVip,
        )
    }
}

@Composable
fun SettingsSection(
    onSettingsUIAction: (SettingsUIAction) -> Unit,
) {
    Column {
        SettingTitle(
            title = stringResource(id = R.string.settings),
        )
        SettingItem(
            title = "Notifications and Sounds",
            icon = Icons.Outlined.Notifications,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnNotificationsClick)
            },
        )
        SettingItem(
            title  = "Chat Settings",
            icon = Icons.Outlined.ChatBubbleOutline,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnNotificationsClick)
            },
        )
        SettingItem(
            title = stringResource(id = R.string.security),
            icon = Icons.Outlined.Security,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnSecurityClick)
            },
        )
        SettingItem(
            title = stringResource(id = R.string.devices),
            icon = Icons.Outlined.Computer,
            onClick = {
            },
        )
//        SettingItem("Payment Methods", Icons.Outlined.CreditCard) {
//            onSettingsUIAction(SettingsUIAction.OnAddPaymentClick)
//        }
        SettingItem(
            stringResource(id = R.string.recharge_coins),
            Icons.Outlined.CreditCard,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnRechargeClick)
            },
        )
//        SettingItem("Payment History", Icons.Outlined.CreditCard) {
//            onSettingsUIAction(SettingsUIAction.OnPaymentHistoryClick)
//        }
        SettingItem(
            title = stringResource(id = R.string.language),
            Icons.Outlined.Language,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnLanguageClick)
            },
        )
        SettingItem(
            title = stringResource(id = R.string.theme),
            icon = Icons.Outlined.Palette,
            onClick = {
                onSettingsUIAction(SettingsUIAction.OnThemeClick)
            },
        )
        SettingItem(
            title = stringResource(id = R.string.about),
            icon = Icons.Outlined.Info,
            onClick = { },
        )
    }
}

@Composable
fun RedButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
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
        shape = MaterialTheme.shapes.small,
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
