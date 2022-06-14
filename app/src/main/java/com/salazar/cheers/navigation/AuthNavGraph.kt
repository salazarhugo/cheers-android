package com.salazar.cheers.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.salazar.cheers.ui.auth.register.RegisterRoute
import com.salazar.cheers.ui.auth.signin.SignInRoute
import com.salazar.cheers.ui.auth.signup.SignUpRoute

fun NavGraphBuilder.authNavGraph(
    navActions: CheersNavigationActions,
) {
    val uri = "https://cheers-a275e.web.app"

    navigation(
        route = CheersDestinations.AUTH_ROUTE,
        startDestination = AuthDestinations.SIGN_IN_ROUTE,
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
            RegisterRoute(navActions = navActions)
        }

        composable(
            route = AuthDestinations.SIGN_IN_ROUTE,
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/signIn/{emailLink}" }),
        ) {
            SignInRoute(
                navActions = navActions,
            )
        }
    }
}