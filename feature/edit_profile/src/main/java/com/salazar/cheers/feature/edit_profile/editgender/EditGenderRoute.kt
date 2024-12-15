package com.salazar.cheers.feature.edit_profile.editgender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.edit_profile.EditProfileViewModel

@Composable
fun EditGenderRoute(
    viewModel: EditProfileViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val selectedGender = uiState.user.gender

    EditGenderScreen(
        gender = selectedGender,
        onGenderClick = viewModel::updateGender,
        navigateBack = navigateBack,
    )
}