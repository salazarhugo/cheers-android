package com.salazar.cheers.feature.edit_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditProfileRoute(
    navigateBack: () -> Unit,
    navigateToGender: () -> Unit,
    navigateToJob: () -> Unit,
    viewModel: EditProfileViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        onWebsiteChanged = viewModel::onWebsiteChanged,
        onNameChanged = viewModel::onNameChanged,
        onBioChanged = viewModel::onBioChanged,
        onUsernameChanged = viewModel::onUsernameChange,
        onSelectImage = viewModel::onSelectPicture,
        onSelectBanner = viewModel::onSelectBanner,
        onDismiss = { navigateBack() },
        onSave = {
            viewModel.onSave()
            navigateBack()
        },
        onDrinkClick = viewModel::selectDrink,
        onGenderClick = navigateToGender,
        onJobClick = navigateToJob,
        onDeleteBanner = viewModel::onDeleteBanner,
    )
}