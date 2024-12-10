package com.salazar.cheers.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.profile.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data object Profile

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.BASE_URL}/profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(
        route = Profile,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.profileScreen(
    navigateBack: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToProfileMore: (String) -> Unit,
    navigateToPostMore: (String) -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToFriendList: () -> Unit,
    navigateToPostDetails: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable<Profile>(
        deepLinks = listOf(
            navDeepLink<Profile>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        ProfileRoute(
            navigateBack = navigateBack,
            navigateToEditProfile = navigateToEditProfile,
            navigateToProfileMore = navigateToProfileMore,
            navigateToSignIn = navigateToSignIn,
            navigateToSignUp = navigateToSignUp,
            navigateToFriendList = navigateToFriendList,
            navigateToPostDetails = navigateToPostDetails,
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToPostMore = navigateToPostMore,
        )
    }
}