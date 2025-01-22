package com.salazar.cheers.feature.signup

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val EMAIL = "email"
const val signUpNavigationRoute = "sign_up_route?$EMAIL={$EMAIL}"
private const val DEEP_LINK_URI_PATTERN =
    "https://cheers-a275e.web.app/signUp/{emailLink}"

fun NavController.navigateToSignUp(
    email: String? = null,
    navOptions: NavOptions? = null,
) {
    val route = if (email != null)
        "sign_up_route?$EMAIL=$email"
    else
        "sign_up_route"
    this.navigate(route, navOptions)
}

fun NavGraphBuilder.signUpScreen(
    navigateToHome: () -> Unit,
) {
    composable(
        route = signUpNavigationRoute,
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
        },
    ) {
        SignUpRoute(
            navigateToHome = navigateToHome,
        )
    }
}