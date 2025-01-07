package com.salazar.cheers.feature.edit_profile.editjob

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.edit_profile.EditProfileViewModel

@Composable
fun EditJobRoute(
    viewModel: EditProfileViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditJobScreen(
        jobTitle = uiState.user.jobTitle,
        jobCompany = uiState.user.jobCompany,
        navigateBack = navigateBack,
        onJobTitleChange = viewModel::onJobTitleChange,
        onJobCompanyChange = viewModel::onJobCompanyChange,
    )
}