package com.salazar.cheers.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.SettingDestinations
import com.salazar.cheers.feature.settings.language.LanguageRoute
import com.salazar.cheers.feature.settings.notifications.NotificationsRoute
import com.salazar.cheers.feature.settings.security.navigateToSecurity
import com.salazar.cheers.feature.settings.security.securityScreen
import com.salazar.cheers.feature.settings.settingsScreen
import com.salazar.cheers.feature.settings.theme.ThemeRoute
import com.salazar.cheers.ui.CheersAppState
import com.softimpact.feature.passcode.settings.navigateToPasscodeSettings
import com.softimpact.feature.passcode.settings.passcodeSettingsScreen

fun NavGraphBuilder.settingNavGraph(
    appState: CheersAppState,
) {
    val navActions = appState.navActions
    val navController = appState.navController

    navigation(
        route = CheersDestinations.SETTING_ROUTE,
        startDestination = SettingDestinations.SETTINGS_ROUTE,
    ) {

        settingsScreen(
            navigateBack = navController::popBackStack,
            navigateToAddPaymentMethod = {},
            navigateToLanguage = {},
            navigateToNotifications = {},
            navigateToTheme = {},
            navigateToRecharge = {},
            navigateToSecurity = navController::navigateToSecurity,
            navigateToPaymentHistory = {},
            navigateToSignIn = {},
            navigateToDeleteAccount = {},
        )

        passcodeSettingsScreen(
            navigateBack = navController::popBackStack,
            navigateToSetPasscode = {},
        )

        securityScreen(
            navigateBack = navController::popBackStack,
            navigateToPassword = {},
            navigateToPasscode = navController::navigateToPasscodeSettings,
        )

        composable(
            route = SettingDestinations.PAYMENT_HISTORY_ROUTE,
        ) {
            com.salazar.cheers.feature.settings.payments.PaymentHistoryRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.RECHARGE_ROUTE,
        ) {
            com.salazar.cheers.feature.settings.payments.RechargeRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.ADD_PAYMENT_METHOD_ROUTE,
        ) {
//            AddPaymentMethod(
//                addPaymentViewModel = addPaymentViewModel,
//                navActions = navActions,
//                onAddCard = {}
//            )
        }

        composable(
            route = SettingDestinations.LANGUAGE_ROUTE,
        ) {
            val settingsViewModel = hiltViewModel<com.salazar.cheers.feature.settings.SettingsViewModel>()

            LanguageRoute(
                navActions = navActions,
                settingsViewModel = settingsViewModel,
            )
        }

        composable(
            route = SettingDestinations.NOTIFICATIONS_ROUTE,
        ) {
            NotificationsRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.THEME_ROUTE,
        ) {
            ThemeRoute(
                navActions = navActions,
            )
        }
    }
}