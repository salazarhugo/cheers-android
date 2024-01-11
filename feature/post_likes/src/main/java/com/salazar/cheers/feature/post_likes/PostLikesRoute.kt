package com.salazar.cheers.feature.post_likes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostLikesRoute(
    viewModel: PostLikesViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {},
    navigateToUser: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostLikesScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onUserClick = navigateToUser,
        onPullRefresh = { viewModel.onPullRefresh() },
    )
}