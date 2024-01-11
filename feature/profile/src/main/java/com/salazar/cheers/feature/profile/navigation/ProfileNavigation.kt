package com.salazar.cheers.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.profile.profile.ProfileRoute

const val profileNavigationRoute = "profile_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://profilearty.app/profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(profileNavigationRoute, navOptions)
}

fun NavGraphBuilder.profileScreen(
    navigateBack: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToProfileMore: (String) -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToFriendList: () -> Unit,
) {
    composable(
        route = profileNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        ProfileRoute(
            navigateToEditProfile = navigateToEditProfile,
            navigateToProfileMore = navigateToProfileMore,
            navigateToSignIn = navigateToSignIn,
            navigateToSignUp = navigateToSignUp,
            navigateToFriendList = navigateToFriendList,
            navigateBack = navigateBack,
        )
    }
}