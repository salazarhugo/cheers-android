package com.salazar.cheers.feature.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.material.bottomSheet
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.map.screens.settings.MapSettingsRoute

const val mapSettingsNavigationRoute = "map_settings_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mapSettingsarty.app/map_settings"

fun NavController.navigateToMapSettings(navOptions: NavOptions? = null) {
    this.navigate(mapSettingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.mapSettingsScreen(
    navigateBack: () -> Unit,
) {
    bottomSheet(
        route = mapSettingsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        MapSettingsRoute(
            navigateBack = navigateBack,
        )
    }
}