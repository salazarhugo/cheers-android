package com.salazar.cheers.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.ui.ui.SettingDestinations
import com.salazar.cheers.feature.settings.SettingsRoute
import com.salazar.cheers.feature.settings.SettingsViewModel
import com.salazar.cheers.feature.settings.language.LanguageRoute
import com.salazar.cheers.feature.settings.navigation.settingsScreen
import com.salazar.cheers.feature.settings.notifications.NotificationsRoute
import com.salazar.cheers.feature.settings.password.CreatePasswordRoute
import com.salazar.cheers.feature.settings.payments.PaymentHistoryRoute
import com.salazar.cheers.feature.settings.payments.RechargeRoute
import com.salazar.cheers.feature.settings.security.SecurityRoute
import com.salazar.cheers.feature.settings.theme.ThemeRoute
import com.salazar.cheers.feature.signin.navigateToSignIn
import com.salazar.cheers.ui.CheersAppState

fun NavGraphBuilder.settingNavGraph(
    appState: CheersAppState,
) {
    val navActions = appState.navActions
    val navController = appState.navController

    navigation(
        route = CheersDestinations.SETTING_ROUTE,
        startDestination = SettingDestinations.SETTINGS_ROUTE,
    ) {

        composable(
            route = "${SettingDestinations.PASSWORD_ROUTE}/{hasPassword}",
            arguments = listOf(
                navArgument("hasPassword") {
                    type = NavType.BoolType
                }
            ),
        ) {
            com.salazar.cheers.feature.settings.password.CreatePasswordRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.SECURITY_ROUTE,
        ) {
            SecurityRoute(
                navActions = navActions,
            )
        }

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

        settingsScreen(
            navigateToSignIn = navController::navigateToSignIn,
        )
    }
}