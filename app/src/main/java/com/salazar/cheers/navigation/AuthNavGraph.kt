package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.salazar.cheers.auth.ui.register.RegisterRoute
import com.salazar.cheers.auth.ui.signup.SignUpRoute
import com.salazar.cheers.core.ui.ui.AuthDestinations
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.home.navigation.navigateToHome
import com.salazar.cheers.feature.parties.navigateToParties
import com.salazar.cheers.feature.signin.signInNavigationRoute
import com.salazar.cheers.feature.signin.signInScreen

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
            route = "${AuthDestinations.SIGN_UP_ROUTE}?email={email}&displayName={displayName}",
            arguments = listOf(
                navArgument("email") { nullable = true },
                navArgument("displayName") { nullable = true },
            ),
        ) {
            SignUpRoute(
                navActions = navActions,
            )
        }

        composable(
            route = "${AuthDestinations.REGISTER_ROUTE}/{emailLink}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/register/{emailLink}" }),
        ) {
            RegisterRoute(
                navActions = navActions,
            )
        }

        signInScreen(
            navigateToHome = {
                navController.navigateToParties()
            },
            navigateToSignUp = {
                navActions.navigateToSignUp()
            },
            navigateToRegister = {
                navActions.navigateToRegister()
            },
            navigateToPhone = {},
        )
    }
}