package com.salazar.cheers.feature.home.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.Settings
import com.salazar.cheers.core.ui.navigation.PartyDetailScreen
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
    navigateToMyParties: () -> Unit,
) {
    composable<Home>(
        deepLinks = listOf(
            navDeepLink<Home>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        exitTransition = {
            val toPartyDetails = targetState.destination.hasRoute(PartyDetailScreen("")::class)
            if (toPartyDetails) {
                slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
            } else {
                null
            }
        },
        popEnterTransition = {
            val fromPartyDetails = initialState.destination.hasRoute(PartyDetailScreen("")::class)
            if (fromPartyDetails) {
                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
            } else {
                null
            }
        }
    ) {
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
            navigateToMyParties = navigateToMyParties,
        )
    }
}
