package com.salazar.cheers.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navArgument
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

        composable(
            route = "${AuthDestinations.SIGN_UP_ROUTE}?email={email}&displayName={displayName}",
            arguments = listOf(
                navArgument("email") { nullable = true },
                navArgument("displayName") { nullable = true },
            ),
        ) {
            val signUpViewModel = hiltViewModel<SignUpViewModel>()
            val email = it.arguments?.getString("email")
            val displayName = it.arguments?.getString("displayName")

            if (displayName != null)
                signUpViewModel.onNameChange(name = displayName)

            if (email != null) {
                signUpViewModel.onEmailChange(email = email)
                signUpViewModel.updateWithGoogle(withGoogle = true)
            }

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