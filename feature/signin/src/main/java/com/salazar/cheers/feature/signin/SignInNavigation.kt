package com.salazar.cheers.feature.signin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val signInNavigationRoute = "sign_in_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://cheers-a275e.web.app/signIn/{emailLink}"

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(signInNavigationRoute, navOptions)
}

fun NavGraphBuilder.signInScreen(
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToSignUp: (String?) -> Unit,
) {
    composable(
        route = signInNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SignInRoute(
            navigateToHome = navigateToHome,
            navigateToRegister = navigateToRegister,
            navigateToSignUp = navigateToSignUp,
        )
    }
}