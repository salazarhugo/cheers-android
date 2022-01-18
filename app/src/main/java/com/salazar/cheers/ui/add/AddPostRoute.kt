package com.salazar.cheers.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions
import com.salazar.cheers.internal.User
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

    if (uiState.isChooseOnMapOpen)
        ChooseOnMapScreen(
            onSelectLocation = { addPostViewModel.updateLocationPoint(it); addPostViewModel.interactedWithAddPost() },
            onBackPressed = addPostViewModel::interactedWithAddPost,
        )
    else
        AddPostScreen(
            uiState = uiState,
            profilePictureUrl= profilePictureUrl,
            onCaptionChanged = addPostViewModel::onCaptionChanged,
            onSelectLocation = addPostViewModel::selectLocation,
            onUploadPost = addPostViewModel::uploadPost,
            onDismiss = navActions.navigateBack,
            onShowOnMapChanged = addPostViewModel::onShowOnMapChanged,
            interactWithChooseOnMap = addPostViewModel::interactedWithChooseOnMap,
            navigateToTagUser = {},
            navigateToCamera = { navActions.navigateToCamera() },
            unselectLocation = addPostViewModel::unselectLocation,
            updateLocationName = addPostViewModel::updateLocation,
            updateLocationResults = addPostViewModel::updateLocationResults,
            onSelectMedia = addPostViewModel::setPostImage,
        )
}