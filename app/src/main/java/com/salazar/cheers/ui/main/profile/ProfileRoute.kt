package com.salazar.cheers.ui.main.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Profile screen.
 *
 * @param profileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navActions: CheersNavigationActions,
    username: String,
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = profileViewModel::refresh,
        onStatClicked = { statName, username ->
            navActions.navigateToProfileStats(
                statName,
                username
            )
        },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onPostLike = profileViewModel::toggleLike,
        onEditProfileClicked = { navActions.navigateToEditProfile() },
        onDrinkingStatsClick = { navActions.navigateToDrinkingStats(username) },
        navigateToProfileMoreSheet = navActions.navigateToProfileMoreSheet,
        onWebsiteClicked = { website ->
            var url = website
            if (!url.startsWith("www.") && !url.startsWith("http://"))
                url = "www.$url"
            if (!url.startsWith("http://"))
                url = "http://$url"
            uriHandler.openUri(url)
        }
    )
}