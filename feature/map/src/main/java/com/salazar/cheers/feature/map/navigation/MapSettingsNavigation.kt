package com.salazar.cheers.feature.map.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.map.screens.settings.MapSettingsRoute
import kotlinx.serialization.Serializable

@Serializable
data object MapSettingsScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/map/settings"

fun NavController.navigateToMapSettings(navOptions: NavOptions? = null) {
    navigate(
        route = MapSettingsScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.mapSettingsScreen(
    navigateBack: () -> Unit,
) {
    composable<MapSettingsScreen>(
        deepLinks = listOf(
            navDeepLink<MapSettingsScreen>(basePath = DEEP_LINK_URI_PATTERN)
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
        }
    ) {
        MapSettingsRoute(
            navigateBack = navigateBack,
        )
    }
}