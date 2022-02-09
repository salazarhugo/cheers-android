package com.salazar.cheers.ui.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.map.ChooseOnMapScreen

/**
 * Stateful composable that displays the Navigation route for the Add post screen.
 *
 * @param addPostViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun AddPostRoute(
    profilePictureUrl: String,
    addPostViewModel: AddPostViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by addPostViewModel.uiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null) return@rememberLauncherForActivityResult
        if (it.toString().contains("image"))
            addPostViewModel.setPostImage(it)
        if (it.toString().contains("video"))
            addPostViewModel.setPostVideo(it)
    }

    if (uiState.isChooseOnMapOpen)
        ChooseOnMapScreen(
            onSelectLocation = { addPostViewModel.updateLocationPoint(it); addPostViewModel.interactedWithAddPost() },
            onBackPressed = addPostViewModel::interactedWithAddPost,
        )
    else if (uiState.isChooseBeverageOpen)
        BeverageScreen(
            onBackPressed = addPostViewModel::interactedWithAddPost,
            onSelectBeverage = {}
        )
    else
        AddPostScreen(
            uiState = uiState,
            profilePictureUrl = profilePictureUrl,
            onCaptionChanged = addPostViewModel::onCaptionChanged,
            onSelectLocation = addPostViewModel::selectLocation,
            onUploadPost = addPostViewModel::uploadPost,
            onDismiss = navActions.navigateBack,
            interactWithChooseOnMap = addPostViewModel::interactedWithChooseOnMap,
            interactWithChooseBeverage = addPostViewModel::interactedWithChooseBeverage,
            navigateToTagUser = {},
            navigateToCamera = { navActions.navigateToCamera() },
            unselectLocation = addPostViewModel::unselectLocation,
            updateLocationName = addPostViewModel::updateLocation,
            updateLocationResults = addPostViewModel::updateLocationResults,
            onSelectMedia = addPostViewModel::setPostImage,
            onMediaSelectorClicked = { launcher.launch("image/*") },
            onSelectPrivacy = addPostViewModel::selectPrivacy,
        )
}