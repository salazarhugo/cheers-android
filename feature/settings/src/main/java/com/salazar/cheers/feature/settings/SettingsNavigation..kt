package com.salazar.cheers.feature.settings

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/settings_note"

@Serializable
data object SettingsNavGraph

@Serializable
data object SettingsScreen

fun NavController.navigateToSettings(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = SettingsScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToAddPaymentMethod: () -> Unit,
    navigateToLanguage: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToTheme: () -> Unit,
    navigateToRecharge: () -> Unit,
    navigateToSecurity: () -> Unit,
    navigateToPaymentHistory: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToDeleteAccount: () -> Unit,
    navigateToPremium: () -> Unit,
) {
    composable<SettingsScreen>(
        deepLinks = listOf(
            navDeepLink<SettingsScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        SettingsRoute(
            navigateBack = navigateBack,
            navigateToAddPaymentMethod = navigateToAddPaymentMethod,
            navigateToLanguage = navigateToLanguage,
            navigateToNotifications = navigateToNotifications,
            navigateToTheme = navigateToTheme,
            navigateToRecharge = navigateToRecharge,
            navigateToSecurity = navigateToSecurity,
            navigateToPaymentHistory = navigateToPaymentHistory,
            navigateToSignIn = navigateToSignIn,
            navigateToDeleteAccount = navigateToDeleteAccount,
            navigateToPremium = navigateToPremium,
        )
    }
}
//            val isFromGraph = this.initialState.destination.hierarchy.any {
//               it.hasRoute(SettingsNavGraph::class)
//            }
