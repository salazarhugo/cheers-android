package com.salazar.cheers.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateToEditProfile: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToProfileMore: (String) -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = profileViewModel::onSwipeRefresh,
        navigateToSignIn = navigateToSignIn,
        onPostMoreClicked = { postId, authorId ->
//            navActions.navigateToPostMoreSheet(postId)
        },
        onStatClicked = { statName, username, verified ->
//            navActions.navigateToOtherProfileStats(
//                statName,
//                username,
//                verified,
//            )
        },
        onPostClicked = {
//            navActions.navigateToPostDetail(it)
        },
        onPostLike = profileViewModel::toggleLike,
        onEditProfileClicked = navigateToEditProfile,
        onDrinkingStatsClick = {
//            navActions.navigateToDrinkingStats(it)
        },
        navigateToProfileMoreSheet = navigateToProfileMore,
        onStoryClick = { username ->
//            navActions.navigateToStoryWithUserId(username)
        },
        onWebsiteClicked = { website ->
            var url = website
            if (!url.startsWith("https://"))
                url = "https://$url"
            uriHandler.openUri(url)
        },
        onCommentClick = {
//            navActions.navigateToComments(it)
        }
    )
}