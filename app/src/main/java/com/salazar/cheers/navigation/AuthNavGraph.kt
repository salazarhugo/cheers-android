package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.salazar.cheers.feature.home.home.navigateToHome
import com.salazar.cheers.feature.signin.SignInScreen
import com.salazar.cheers.feature.signin.signInScreen
import com.salazar.cheers.feature.signup.navigateToSignUp
import com.salazar.cheers.feature.signup.signUpScreen
import kotlinx.serialization.Serializable

@Serializable
data object AuthNavGraph

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
) {
    navigation<AuthNavGraph>(
        startDestination = SignInScreen,
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