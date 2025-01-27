package com.salazar.cheers.feature.signin

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object SignInScreen

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/signin"

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(
        route = SignInScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.signInScreen(
    navigateToHome: () -> Unit,
    navigateToSignUp: (String?) -> Unit,
) {
    composable<SignInScreen>(
        deepLinks = listOf(
            navDeepLink<SignInScreen>(basePath = DEEP_LINK_URI_PATTERN),
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
        },
    ) {
        SignInRoute(
            navigateToHome = navigateToHome,
            navigateToSignUp = navigateToSignUp,
        )
    }
}