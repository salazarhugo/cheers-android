package com.salazar.cheers.feature.profile.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToProfileMore: (String) -> Unit,
    navigateToFriendList: () -> Unit,
    navigateToPostDetails: (String) -> Unit,
    navigateToPostMore: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        navigateToSignIn = navigateToSignIn,
        navigateToSignUp = navigateToSignUp,
        navigateToProfileMoreSheet = navigateToProfileMore,
        onProfileUIAction = { action ->
            when(action) {
                ProfileUIAction.OnBackPressed -> navigateBack()
                ProfileUIAction.OnEditProfileClick -> navigateToEditProfile()
                ProfileUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                ProfileUIAction.OnFriendListClick -> navigateToFriendList()
                is ProfileUIAction.OnPostDetailsClick -> navigateToPostDetails(action.postID)
                is ProfileUIAction.OnUserClick -> navigateToOtherProfile(action.userID)
                is ProfileUIAction.OnPostMoreClick -> navigateToPostMore(action.postID)
            }
        }
    )
}