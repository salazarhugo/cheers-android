package com.salazar.cheers.feature.home.navigation.home

import android.Manifest
import android.os.Build
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.ui.ui.RequestPermission

const val homeNavigationRoute = "home_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onActivityClick: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    navigateToCreatePost: () -> Unit = {},
    navigateToCreateNote: () -> Unit = {},
    navigateToParties: () -> Unit = {},
    navigateToNote: (String) -> Unit = {},
    navigateToMessages: () -> Unit = {},
    navigateToPostMoreSheet: (String) -> Unit = {},
    navigateToPostComments: (String) -> Unit = {},
    navigateToPostLikes: (String) -> Unit = {},
    navigateToUser: (String) -> Unit,
    navigateToSignIn: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToDeletePostDialog: (String) -> Unit,
) {
    composable(
        route = homeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestPermission(permission = Manifest.permission.POST_NOTIFICATIONS)
        }
        HomeRoute(
            onActivityClick = onActivityClick,
            onPostClick = onPostClick,
            navigateToCreatePost = navigateToCreatePost,
            navigateToCreateNote = navigateToCreateNote,
            navigateToNote = navigateToNote,
            navigateToParties = navigateToParties,
            navigateToMessages = navigateToMessages,
            navigateToPostMoreSheet = navigateToPostMoreSheet,
            navigateToPostComments = navigateToPostComments,
            navigateToPostLikes = navigateToPostLikes,
            navigateToUser = navigateToUser,
            navigateToSignIn = navigateToSignIn,
            navigateToCamera = navigateToCamera,
            navigateToDeletePostDialog = navigateToDeletePostDialog,
        )
    }
}