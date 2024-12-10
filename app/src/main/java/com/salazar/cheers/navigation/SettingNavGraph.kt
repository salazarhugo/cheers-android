package com.salazar.cheers.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.MainDestinations
import com.salazar.cheers.core.ui.ui.SettingDestinations
import com.salazar.cheers.feature.premium.navigation.navigateToPremium
import com.salazar.cheers.feature.premium.navigation.premiumScreen
import com.salazar.cheers.feature.settings.language.LanguageRoute
import com.salazar.cheers.feature.settings.notifications.NotificationsRoute
import com.salazar.cheers.feature.settings.password.createPasswordScreen
import com.salazar.cheers.feature.settings.password.navigateToCreatePassword
import com.salazar.cheers.feature.settings.recharge.RechargeRoute
import com.salazar.cheers.feature.settings.recharge.navigateToRecharge
import com.salazar.cheers.feature.settings.recharge.rechargeScreen
import com.salazar.cheers.feature.settings.security.navigateToSecurity
import com.salazar.cheers.feature.settings.security.securityScreen
import com.salazar.cheers.feature.settings.settingsNavigationRoute
import com.salazar.cheers.feature.settings.settingsScreen
import com.salazar.cheers.feature.settings.theme.ThemeRoute
import com.salazar.cheers.feature.signin.navigateToSignIn
import com.salazar.cheers.ui.CheersAppState
import com.softimpact.feature.passcode.change.changePasscodeScreen
import com.softimpact.feature.passcode.change.navigateToChangePasscode
import com.softimpact.feature.passcode.create.createPasscodeScreen
import com.softimpact.feature.passcode.create.navigateToCreatePasscode
import com.softimpact.feature.passcode.settings.navigateToPasscodeSettings
import com.softimpact.feature.passcode.settings.passcodeSettingsScreen

fun NavGraphBuilder.settingNavGraph(
    appState: CheersAppState,
) {
    val navActions = appState.navActions
    val navController = appState.navController

    navigation(
        route = CheersDestinations.SETTING_ROUTE,
        startDestination = settingsNavigationRoute,
    ) {

        settingsScreen(
            navigateBack = navController::popBackStack,
            navigateToAddPaymentMethod = {},
            navigateToLanguage = {},
            navigateToNotifications = {},
            navigateToTheme = {
                navController.navigate(SettingDestinations.THEME_ROUTE)
            },
            navigateToRecharge = navController::navigateToRecharge,
            navigateToSecurity = navController::navigateToSecurity,
            navigateToPaymentHistory = {},
            navigateToSignIn = navController::navigateToSignIn,
            navigateToDeleteAccount = { navController.navigate(MainDestinations.ACCOUNT_DELETE) },
            navigateToPremium = navController::navigateToPremium,
        )

        premiumScreen(
            navigateBack = navController::popBackStack,
        )

        createPasscodeScreen(
            navigateBack = navController::popBackStack,
            navigateToPasscodeSettings = navController::navigateToPasscodeSettings,
        )

        passcodeSettingsScreen(
            navigateBack = navController::popBackStack,
            navigateToChangePasscode = navController::navigateToChangePasscode,
        )

        changePasscodeScreen(
            navigateBack = navController::popBackStack,
        )

        securityScreen(
            navigateBack = navController::popBackStack,
            navigateToPassword = navController::navigateToCreatePassword,
            navigateToPasscodeSettings = navController::navigateToPasscodeSettings,
            navigateToCreatePasscode = navController::navigateToCreatePasscode,
        )

        createPasswordScreen(
            navigateBack = navController::popBackStack,
        )

        composable(
            route = SettingDestinations.PAYMENT_HISTORY_ROUTE,
        ) {
            com.salazar.cheers.feature.settings.payments.PaymentHistoryRoute(
                navActions = navActions,
            )
        }

        rechargeScreen(
            navigateBack = navController::popBackStack,
        )

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
            val settingsViewModel =
                hiltViewModel<com.salazar.cheers.feature.settings.SettingsViewModel>()

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