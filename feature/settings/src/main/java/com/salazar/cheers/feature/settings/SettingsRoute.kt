package com.salazar.cheers.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigateToAddPaymentMethod: () -> Unit,
    navigateToLanguage: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToTheme: () -> Unit,
    navigateToRecharge: () -> Unit,
    navigateToSecurity: () -> Unit,
    navigateToPaymentHistory: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToDeleteAccount: () -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(uiState.signedOut) {
        if (uiState.signedOut)
            navigateToSignIn()
    }
    SettingsScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
        onSignOut = {
            settingsViewModel.onSignOut {
                navigateToSignIn()
            }
        },
        onSettingsUIAction = { action ->
            when (action) {
                is SettingsUIAction.OnThemeClick -> navigateToTheme()
                is SettingsUIAction.OnLanguageClick -> navigateToLanguage()
                is SettingsUIAction.OnRechargeClick -> navigateToRecharge()
                is SettingsUIAction.OnAddPaymentClick -> navigateToAddPaymentMethod()
                is SettingsUIAction.OnPaymentHistoryClick -> navigateToPaymentHistory()
                is SettingsUIAction.OnNotificationsClick -> navigateToNotifications()
                is SettingsUIAction.OnPrivacyPolicyClick -> uriHandler.openUri("https://cheers-a275e.web.app/privacy-policy")
                is SettingsUIAction.OnTermsOfUseClick -> uriHandler.openUri("https://cheers-a275e.web.app/terms-of-use")
                is SettingsUIAction.OnSecurityClick -> navigateToSecurity()
            }
        },
        navigateToBecomeVip = {},
        onDeleteAccount = { navigateToDeleteAccount() },
    )
}

sealed class SettingsUIAction {
    object OnThemeClick : SettingsUIAction()
    object OnNotificationsClick : SettingsUIAction()
    object OnLanguageClick : SettingsUIAction()
    object OnAddPaymentClick : SettingsUIAction()
    object OnPaymentHistoryClick : SettingsUIAction()
    object OnRechargeClick : SettingsUIAction()
    object OnPrivacyPolicyClick : SettingsUIAction()
    object OnTermsOfUseClick : SettingsUIAction()
    object OnSecurityClick : SettingsUIAction()
}
