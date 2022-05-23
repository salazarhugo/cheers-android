package com.salazar.cheers.ui.main.event.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the AddEvent screen.
 *
 * @param addEventViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun AddEventRoute(
    addEventViewModel: AddEventViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by addEventViewModel.uiState.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                addEventViewModel.setPhoto(it)
        }

    AddEventScreen(
        uiState = uiState,
        onPrivacyChange = addEventViewModel::setPrivacy,
        onNameChange = addEventViewModel::setName,
        onQueryChange = addEventViewModel::onQueryChange,
        onLocationClick = addEventViewModel::onLocationClick,
        onDescriptionChange = addEventViewModel::onDescriptionChange,
        onStartTimeSecondsChange = addEventViewModel::onStartTimeSecondsChange,
        onEndTimeSecondsChange = addEventViewModel::onEndTimeSecondsChange,
        onAddEventUIAction = {
            when(it) {
                AddEventUIAction.OnDismiss -> {
                    navActions.navigateBack()
                }
                AddEventUIAction.OnAddPeopleClick -> TODO()
                AddEventUIAction.OnShowMapChange -> TODO()
                AddEventUIAction.OnUploadEvent -> {
                    addEventViewModel.uploadEvent()
                    navActions.navigateBack()
                }
                AddEventUIAction.OnAddPhoto -> {
                     launcher.launch("image/*")
                }
                AddEventUIAction.OnEventDetailsClick ->  {
                }
                AddEventUIAction.OnHasEndDateToggle ->  addEventViewModel.hasEndDateToggle()
            }
        },
    )
}
