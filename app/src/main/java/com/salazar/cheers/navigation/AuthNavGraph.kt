package com.salazar.cheers.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.salazar.cheers.ui.auth.signin.SignInRoute
import com.salazar.cheers.ui.auth.signin.SignInViewModel
import com.salazar.cheers.ui.auth.signup.SignUpRoute
import com.salazar.cheers.ui.auth.signup.SignUpViewModel

fun NavGraphBuilder.authNavGraph(
    navActions: CheersNavigationActions,
) {
    navigation(
        route = CheersDestinations.AUTH_ROUTE,
        startDestination = AuthDestinations.SIGN_IN_ROUTE,
    ) {
        composable(AuthDestinations.SIGN_UP_ROUTE) {
            val signUpViewModel = hiltViewModel<SignUpViewModel>()

            SignUpRoute(
                signUpViewModel = signUpViewModel,
                navActions = navActions,
            )
        }
        composable(AuthDestinations.SIGN_IN_ROUTE) {
            val signInViewModel = hiltViewModel<SignInViewModel>()

            SignInRoute(
                signInViewModel = signInViewModel,
                navActions = navActions,
            )
        }
    }
}