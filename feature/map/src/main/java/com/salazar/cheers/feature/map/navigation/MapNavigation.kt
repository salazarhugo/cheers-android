package com.salazar.cheers.feature.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.map.screens.map.MapRoute
import kotlinx.serialization.Serializable

@Serializable
data object MapScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/map"

fun NavController.navigateToMap(navOptions: NavOptions? = null) {
    navigate(
        route = MapScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.mapScreen(
    navigateBack: () -> Unit,
    navigateToMapSettings: () -> Unit,
    navigateToCreatePost: () -> Unit,
    navigateToChat: (UserItem) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable<MapScreen>(
        deepLinks = listOf(
            navDeepLink<MapScreen>(basePath = DEEP_LINK_URI_PATTERN)
        ),
    ) {
        MapRoute(
            navigateBack = navigateBack,
            navigateToCreatePost = navigateToCreatePost,
            navigateToMapSettings = navigateToMapSettings,
            navigateToChatWithUserId = navigateToChat,
            navigateToOtherProfile = navigateToOtherProfile,
        )
    }
}