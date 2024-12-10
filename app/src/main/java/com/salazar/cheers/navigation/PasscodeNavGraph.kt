package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.salazar.cheers.R
import com.salazar.cheers.feature.home.home.navigateToHome
import com.softimpact.feature.passcode.passcode.Passcode
import com.softimpact.feature.passcode.passcode.passcodeScreen
import kotlinx.serialization.Serializable

@Serializable
data object PasscodeNavGraph

fun NavGraphBuilder.passcodeNavGraph(
    navController: NavController,
) {
    navigation<PasscodeNavGraph>(
        startDestination = Passcode,
    ) {
        passcodeScreen(
            banner = R.drawable.ic_splash,
            navigateToHome = navController::navigateToHome,
        )
    }
}
