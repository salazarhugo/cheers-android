package com.salazar.cheers.feature.edit_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditProfileRoute(
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by editProfileViewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        onWebsiteChanged = editProfileViewModel::onWebsiteChanged,
        onNameChanged = editProfileViewModel::onNameChanged,
        onBioChanged = editProfileViewModel::onBioChanged,
        onUsernameChanged = editProfileViewModel::onUsernameChange,
        onSelectImage = editProfileViewModel::onSelectPicture,
        onDismiss = { navigateBack() },
        onSave = {
            editProfileViewModel.updateUser()
            navigateBack()
        },
    )
}