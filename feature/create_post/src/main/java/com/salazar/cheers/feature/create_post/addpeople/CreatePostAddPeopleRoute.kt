package com.salazar.cheers.feature.create_post.addpeople

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.create_post.CreatePostViewModel

@Composable
fun CreatePostAddPeopleRoute(
    navigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreatePostAddPeopleScreen(
        selectedUsers = uiState.selectedTagUsers,
        onSelectPeople = viewModel::selectTagUser,
        navigateBack = navigateBack,
    )
}
