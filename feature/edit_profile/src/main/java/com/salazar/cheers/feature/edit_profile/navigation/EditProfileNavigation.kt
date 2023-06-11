package com.salazar.cheers.feature.edit_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.edit_profile.EditProfileRoute

const val editProfileNavigationRoute = "edit_profile_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/edit_profile"

fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) {
    this.navigate(editProfileNavigationRoute, navOptions)
}

fun NavGraphBuilder.editProfileScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = editProfileNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        EditProfileRoute(
            navigateBack = navigateBack,
        )
    }
}
