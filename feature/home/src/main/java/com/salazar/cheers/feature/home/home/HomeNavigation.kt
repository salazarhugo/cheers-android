package com.salazar.cheers.feature.home.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.Settings
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/home"

@Serializable
data object Home

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
    appSettings: Settings,
    onActivityClick: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    navigateToCreatePost: () -> Unit = {},
    navigateToCreateNote: () -> Unit = {},
    navigateToCreateParty: () -> Unit,
    navigateToParties: () -> Unit = {},
    navigateToMessages: () -> Unit = {},
    navigateToPostMoreSheet: (String) -> Unit = {},
    navigateToPostComments: (String) -> Unit = {},
    navigateToPostLikes: (String) -> Unit = {},
    navigateToUser: (String) -> Unit,
    navigateToSignIn: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToDeletePostDialog: (String) -> Unit,
    navigateToPartyDetail: (String) -> Unit,
    navigateToMap: () -> Unit = {},
) {
    composable<Home>(deepLinks = listOf(navDeepLink<Home>(basePath = DEEP_LINK_URI_PATTERN))) {
        HomeRoute(
            appSettings = appSettings,
            onActivityClick = onActivityClick,
            onPostClick = onPostClick,
            navigateToCreatePost = navigateToCreatePost,
            navigateToCreateNote = navigateToCreateNote,
            navigateToParties = navigateToParties,
            navigateToMessages = navigateToMessages,
            navigateToPostMoreSheet = navigateToPostMoreSheet,
            navigateToPostComments = navigateToPostComments,
            navigateToPostLikes = navigateToPostLikes,
            navigateToUser = navigateToUser,
            navigateToSignIn = navigateToSignIn,
            navigateToCamera = navigateToCamera,
            navigateToDeletePostDialog = navigateToDeletePostDialog,
            navigateToPartyDetail = navigateToPartyDetail,
            navigateToCreateParty = navigateToCreateParty,
            navigateToMap = navigateToMap,
        )
    }
}
