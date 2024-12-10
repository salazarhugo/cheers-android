package com.salazar.cheers.ui.main.party.create.recap

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.ui.main.party.create.CreatePartyUIAction
import com.salazar.cheers.ui.main.party.create.CreatePartyViewModel


@Composable
fun CreatePartyRecapStateful(
    viewModel: CreatePartyViewModel,
    navigateBack: () -> Unit,
    navigateToBasicInfo: () -> Unit,
    navigateToLocation: () -> Unit,
    navigateToDescription: () -> Unit,
    navigateToUserProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.setPhoto(it)
        }

    CreatePartyRecapScreen(
        uiState = uiState,
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

                CreatePartyUIAction.OnPartyDetailsClick -> navigateToBasicInfo()

                CreatePartyUIAction.OnHasEndDateToggle -> viewModel.hasEndDateToggle()
                CreatePartyUIAction.OnLocationClick -> navigateToLocation()
                is CreatePartyUIAction.OnPrivacyChange -> viewModel.setPrivacy(it.privacy)
                CreatePartyUIAction.OnDescriptionClick -> navigateToDescription()
                CreatePartyUIAction.OnBasicInfoClick -> navigateToBasicInfo()
                is CreatePartyUIAction.OnUserClick -> navigateToUserProfile(it.username)
                else -> {}
            }
        },
    )
}