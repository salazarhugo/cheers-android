package com.salazar.cheers.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.util.Constants

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
    navigateToPremium: () -> Unit,
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
                is SettingsUIAction.OnPrivacyPolicyClick -> uriHandler.openUri(Constants.PRIVACY_POLICY_LINK)
                is SettingsUIAction.OnTermsOfUseClick -> uriHandler.openUri(Constants.TERMS_AND_CONDITIONS_LINK)
                is SettingsUIAction.OnSecurityClick -> navigateToSecurity()
                SettingsUIAction.OnRequestNewFeatureClick -> uriHandler.openUri(Constants.FEEDBACK_LINK)
            }
        },
        navigateToBecomeVip = navigateToPremium,
        onDeleteAccount = navigateToDeleteAccount,
    )
}

sealed class SettingsUIAction {
    data object OnThemeClick : SettingsUIAction()
    data object OnNotificationsClick : SettingsUIAction()
    data object OnLanguageClick : SettingsUIAction()
    data object OnAddPaymentClick : SettingsUIAction()
    data object OnPaymentHistoryClick : SettingsUIAction()
    data object OnRechargeClick : SettingsUIAction()
    data object OnPrivacyPolicyClick : SettingsUIAction()
    data object OnRequestNewFeatureClick : SettingsUIAction()
    data object OnTermsOfUseClick : SettingsUIAction()
    data object OnSecurityClick : SettingsUIAction()
}
