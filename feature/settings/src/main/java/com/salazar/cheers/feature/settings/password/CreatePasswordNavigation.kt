package com.salazar.cheers.feature.settings.password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val createPasswordNavigationRoute = "create_password_route"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/create_password"

fun NavController.navigateToCreatePassword(
    navOptions: NavOptions? = null,
) {
    this.navigate(createPasswordNavigationRoute, navOptions)
}

fun NavGraphBuilder.createPasswordScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = createPasswordNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        CreatePasswordRoute(
            navigateBack = navigateBack,
        )
    }
}
