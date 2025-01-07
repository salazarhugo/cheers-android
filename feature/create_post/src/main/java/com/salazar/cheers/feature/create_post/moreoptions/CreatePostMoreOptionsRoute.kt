package com.salazar.cheers.feature.create_post.moreoptions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.create_post.CreatePostUIAction
import com.salazar.cheers.feature.create_post.CreatePostViewModel

@Composable
fun CreatePostMoreOptionsRoute(
    navigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreatePostMoreOptionsScreen(
        notificationEnabled = uiState.notify,
        likesEnabled = uiState.likesEnabled,
        commentsEnabled = uiState.commentsEnabled,
        shareEnabled = uiState.shareEnabled,
        onCreatePostMoreOptionsUIAction = {
            when (it) {
                CreatePostUIAction.OnBackPressed -> navigateBack()
                CreatePostUIAction.OnSwipeRefresh -> {}
                is CreatePostUIAction.OnNotificationChange -> viewModel.toggleNotify(
                    it.enabled
                )

                is CreatePostUIAction.OnEnableCommentsChange -> viewModel.onEnableCommentsChange(
                    it.enabled
                )

                is CreatePostUIAction.OnEnableShareChange -> viewModel.onEnableShareChange(
                    it.enabled
                )

                is CreatePostUIAction.OnEnableLikesChange -> viewModel.onEnabledLikesChange(
                    it.enabled
                )

                else -> Unit
            }
        },
    )
}
