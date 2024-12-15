package com.softimpact.feature.passcode.passcode

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object Passcode

fun NavController.navigateToPasscode(navOptions: NavOptions? = null) {
    this.navigate(Passcode, navOptions)
}

fun NavGraphBuilder.passcodeScreen(
    banner: Int,
    navigateToHome: () -> Unit,
) {
    composable<Passcode>(
        deepLinks = listOf(
            navDeepLink<Passcode>(basePath = "${Constants.DEEPLINK_BASE_URL}/passcode")
        ),
    ) {
        PasscodeRoute(
            banner = banner,
            navigateToHome = navigateToHome,
        )
    }
}
