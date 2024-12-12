package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.feature.home.home.navigateToHome
import com.salazar.cheers.feature.signin.signInScreen
import com.salazar.cheers.feature.signup.navigateToSignUp
import com.salazar.cheers.feature.signup.signUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    startDestination: String,
) {
    navigation(
        route = CheersDestinations.AUTH_ROUTE,
        startDestination = startDestination,
    ) {
        signUpScreen(
            navigateToHome = navController::navigateToHome,
        )

        signInScreen(
            navigateToHome = navController::navigateToHome,
            navigateToSignUp = navController::navigateToSignUp,
        )
    }
}