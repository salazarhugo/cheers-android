package com.salazar.cheers.feature.premium.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable

const val premiumNavigationRoute = "premium_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://cheers.social/premium"

fun NavController.navigateToPremium(navOptions: NavOptions? = null) {
    this.navigate(premiumNavigationRoute, navOptions)
}

fun NavGraphBuilder.premiumScreen(
    navigateBack: () -> Unit,
    navigateToWelcomeCheersPremium: () -> Unit,
) {
    composable(
        route = premiumNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
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
        PremiumRoute(
            onBackPressed = navigateBack,
            navigateToWelcomeCheersPremium = navigateToWelcomeCheersPremium,
        )
    }
}

@Serializable
data object PremiumWelcomeScreen

fun NavController.navigateToWelcomePremium(navOptions: NavOptions? = null) {
    this.navigate(
        route = PremiumWelcomeScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.premiumWelcomeScreen(
    navigateToProfile: () -> Unit,
) {
    composable<PremiumWelcomeScreen> {
        SuccessPurchaseLoadingScreen(
            navigateToProfile = navigateToProfile,
        )
    }
}
