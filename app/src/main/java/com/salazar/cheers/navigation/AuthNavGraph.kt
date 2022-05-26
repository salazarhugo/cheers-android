package com.salazar.cheers.navigation

import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.salazar.cheers.ui.auth.register.RegisterRoute
import com.salazar.cheers.ui.auth.signin.SignInRoute
import com.salazar.cheers.ui.auth.signin.SignInViewModel
import com.salazar.cheers.ui.auth.signup.SignUpRoute
import com.salazar.cheers.ui.auth.signup.SignUpViewModel

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

        composable(
            route = "${AuthDestinations.REGISTER_ROUTE}/{username}/{emailLink}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/register/{username}/{emailLink}"}),
        ) {
            RegisterRoute(navActions = navActions)
        }

        composable(
            route = AuthDestinations.SIGN_IN_ROUTE,
            deepLinks = listOf(),
        ) {
            val signInViewModel = hiltViewModel<SignInViewModel>()

            SignInRoute(
                signInViewModel = signInViewModel,
                navActions = navActions,
            )
        }

    }
}