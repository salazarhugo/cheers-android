package com.salazar.cheers.ui.main.party.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreatePartyRoute(
    viewModel: CreatePartyViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.setPhoto(it)
        }

    CreatePartyScreen(
        uiState = uiState,
        onPrivacyChange = viewModel::setPrivacy,
        onNameChange = viewModel::onNameChange,
        onQueryChange = viewModel::onQueryChange,
        onLocationClick = viewModel::onLocationClick,
        onDescriptionChange = viewModel::onDescriptionChange,
        onStartTimeSecondsChange = viewModel::onStartTimeSecondsChange,
        onEndTimeSecondsChange = viewModel::onEndTimeSecondsChange,
        onShowGuestListToggle = viewModel::onShowGuestListToggle,
        onCreatePartyUIAction = {
            when (it) {
                CreatePartyUIAction.OnDismiss -> {
                    navigateBack()
                }

                CreatePartyUIAction.OnAddPeopleClick -> TODO()
                CreatePartyUIAction.OnShowMapChange -> TODO()
                CreatePartyUIAction.OnUploadParty -> {
                    viewModel.createParty()
                    navigateBack()
                }

                CreatePartyUIAction.OnAddPhoto -> {
                    launcher.launch("image/*")
                }

                CreatePartyUIAction.OnPartyDetailsClick -> {
                }

                CreatePartyUIAction.OnHasEndDateToggle -> viewModel.hasEndDateToggle()
                else -> {}
            }
        },
    )
}
