package com.salazar.cheers.feature.signup

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
    this.navigate("sign_up_route?$EMAIL=$email", navOptions)
}

fun NavGraphBuilder.signUpScreen(
    navigateBack: () -> Unit,
    navigateToSignIn: () -> Unit,
) {
    composable(
        route = signUpNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SignUpRoute(
            navigateToSignIn = navigateToSignIn,
            navigateBack = navigateBack,
        )
    }
}