package com.salazar.cheers.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavArgument
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
import com.salazar.cheers.ui.settings.payments.*
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
            SettingDestinations.SECURITY_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            SecurityRoute(
                navActions = navActions,
            )
        }

        composable(
            SettingDestinations.PAYMENT_HISTORY_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val paymentHistoryViewModel = hiltViewModel<PaymentHistoryViewModel>()

            PaymentHistoryRoute(
                paymentHistoryViewModel = paymentHistoryViewModel,
                navActions = navActions,
            )
        }

        composable(
            SettingDestinations.RECHARGE_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val rechargeViewModel = hiltViewModel<RechargeViewModel>()

            RechargeRoute(
                rechargeViewModel = rechargeViewModel,
                navActions = navActions,
            )
        }

        composable(
            SettingDestinations.ADD_PAYMENT_METHOD_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val addPaymentViewModel = hiltViewModel<AddPaymentViewModel>()

//            AddPaymentMethod(
//                addPaymentViewModel = addPaymentViewModel,
//                navActions = navActions,
//                onAddCard = {}
//            )
        }

        composable(
            SettingDestinations.LANGUAGE_ROUTE,
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
            SettingDestinations.NOTIFICATIONS_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            NotificationsRoute(
                navActions = navActions,
                settingsViewModel = settingsViewModel,
            )
        }

        composable(
            SettingDestinations.THEME_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            ThemeRoute(
                navActions = navActions,
                settingsViewModel = settingsViewModel,
            )
        }

        composable(SettingDestinations.SETTINGS_ROUTE) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(
                settingsViewModel = settingsViewModel,
                navActions = navActions,
            )
        }

    }
}