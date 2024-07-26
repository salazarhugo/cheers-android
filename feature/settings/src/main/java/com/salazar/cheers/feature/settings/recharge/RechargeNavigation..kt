package com.salazar.cheers.feature.settings.recharge

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.settings.security.SecurityRoute

const val rechargeNavigationRoute = "recharge_route"
private const val DEEP_LINK_URI_PATTERN = "https://cheers.social/recharge_note"

fun NavController.navigateToRecharge(
    navOptions: NavOptions? = null,
) {
    this.navigate(rechargeNavigationRoute, navOptions)
}

fun NavGraphBuilder.rechargeScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = rechargeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        RechargeRoute(
            navigateBack = navigateBack,
        )
    }
}
