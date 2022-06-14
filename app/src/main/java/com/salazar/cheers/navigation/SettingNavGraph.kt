package com.salazar.cheers.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.salazar.cheers.ui.settings.SettingsRoute
import com.salazar.cheers.ui.settings.SettingsViewModel
import com.salazar.cheers.ui.settings.language.LanguageRoute
import com.salazar.cheers.ui.settings.notifications.NotificationsRoute
import com.salazar.cheers.ui.settings.password.CreatePasswordRoute
import com.salazar.cheers.ui.settings.payments.AddPaymentViewModel
import com.salazar.cheers.ui.settings.payments.PaymentHistoryRoute
import com.salazar.cheers.ui.settings.payments.RechargeRoute
import com.salazar.cheers.ui.settings.security.SecurityRoute
import com.salazar.cheers.ui.settings.theme.ThemeRoute

fun NavGraphBuilder.settingNavGraph(
    navActions: CheersNavigationActions,
) {
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
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            CreatePasswordRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.SECURITY_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            SecurityRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.PAYMENT_HISTORY_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            PaymentHistoryRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.RECHARGE_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            RechargeRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.ADD_PAYMENT_METHOD_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
//            AddPaymentMethod(
//                addPaymentViewModel = addPaymentViewModel,
//                navActions = navActions,
//                onAddCard = {}
//            )
        }

        composable(
            route = SettingDestinations.LANGUAGE_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            LanguageRoute(
                navActions = navActions,
                settingsViewModel = settingsViewModel,
            )
        }

        composable(
            route = SettingDestinations.NOTIFICATIONS_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            NotificationsRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.THEME_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            ThemeRoute(
                navActions = navActions,
            )
        }

        composable(
            route = SettingDestinations.SETTINGS_ROUTE,
        ) {
            SettingsRoute(
                navActions = navActions,
            )
        }
    }
}