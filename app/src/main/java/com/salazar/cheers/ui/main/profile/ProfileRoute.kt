package com.salazar.cheers.ui.main.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

/**
 * Stateful composable that displays the Navigation route for the Profile screen.
 *
 * @param profileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
    showSnackBar: (String) -> Unit,
    appState: CheersAppState,
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    if (uiState.errorMessages.isNotBlank()) {
        LaunchedEffect(appState.snackBarHostState) {
            showSnackBar(uiState.errorMessages)
        }
    }

    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = profileViewModel::onSwipeRefresh,
        onPostMoreClicked = { postId, authorId ->
            navActions.navigateToPostMoreSheet(postId)
        },
        onStatClicked = { statName, username, verified ->
            navActions.navigateToOtherProfileStats(
                statName,
                username,
                verified,
            )
        },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onPostLike = profileViewModel::toggleLike,
        onEditProfileClicked = { navActions.navigateToEditProfile() },
        onDrinkingStatsClick = { navActions.navigateToDrinkingStats(it) },
        navigateToProfileMoreSheet = { navActions.navigateToProfileMoreSheet(it) },
        onStoryClick = { username ->
            navActions.navigateToStoryWithUserId(username)
        },
        onWebsiteClicked = { website ->
            var url = website
            if (!url.startsWith("https://"))
                url = "https://$url"
            uriHandler.openUri(url)
        },
        onCommentClick = {
            navActions.navigateToComments(it)
        }
    )

}