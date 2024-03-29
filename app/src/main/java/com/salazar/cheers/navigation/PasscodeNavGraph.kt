package com.salazar.cheers.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.feature.home.navigation.home.navigateToHome
import com.softimpact.feature.passcode.passcode.passcodeNavigationRoute
import com.softimpact.feature.passcode.passcode.passcodeScreen

fun NavGraphBuilder.passcodeNavGraph(
    navController: NavController,
) {
    navigation(
        route = CheersDestinations.PASSCODE_ROUTE,
        startDestination = passcodeNavigationRoute,
    ) {
        passcodeScreen(
            banner = R.drawable.ic_artboard_1cheers_logo_svg,
            navigateToHome = navController::navigateToHome,
        )
    }
}
