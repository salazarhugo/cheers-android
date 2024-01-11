package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.salazar.cheers.auth.ui.register.RegisterRoute
import com.salazar.cheers.core.ui.ui.AuthDestinations
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.parties.navigateToParties
import com.salazar.cheers.feature.signin.navigateToSignIn
import com.salazar.cheers.feature.signin.signInScreen
import com.salazar.cheers.feature.signup.navigateToSignUp
import com.salazar.cheers.feature.signup.signUpScreen

fun NavGraphBuilder.authNavGraph(
    navActions: CheersNavigationActions,
    navController: NavController,
    startDestination: String,
) {
    val uri = "https://cheers-a275e.web.app"

    navigation(
        route = CheersDestinations.AUTH_ROUTE,
        startDestination = startDestination,
    ) {

        composable(
            route = "${AuthDestinations.REGISTER_ROUTE}/{emailLink}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/register/{emailLink}" }),
        ) {
            RegisterRoute(
                navActions = navActions,
            )
        }

        signUpScreen(
            navigateToSignIn = navController::navigateToSignIn,
            navigateBack = navController::popBackStack,
        )

        signInScreen(
            navigateToHome = navController::navigateToParties,
            navigateToSignUp = navController::navigateToSignUp,
            navigateToRegister = navActions.navigateToRegister,
        )
    }
}