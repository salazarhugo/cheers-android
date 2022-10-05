package com.salazar.cheers.ui.main.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Profile screen.
 *
 * @param profileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    if (uiState.errorMessages.isNotBlank()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, uiState.errorMessages, Toast.LENGTH_SHORT).show()
        }
    }

    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = profileViewModel::onSwipeRefresh,
        onPostMoreClicked = { postId, authorId ->
            navActions.navigateToPostMoreSheet(
                postId,
                authorId
            )
        },
        onStatClicked = { statName, username, verified ->
            navActions.navigateToProfileStats(
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