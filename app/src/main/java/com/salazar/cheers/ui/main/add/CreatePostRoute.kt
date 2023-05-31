package com.salazar.cheers.ui.main.add

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.map.ui.ChooseOnMapScreen
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Add post screen.
 *
 * @param addPostViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CreatePostRoute(
    navActions: CheersNavigationActions,
    addPostViewModel: CreatePostViewModel = hiltViewModel(),
) {
    val uiState by addPostViewModel.uiState.collectAsStateWithLifecycle()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            if (it.size <= 8)
                addPostViewModel.setPhotos(it)
            else
                addPostViewModel.updateErrorMessage("Maximum 8 photos.")
        }

    BackHandler {
        if (uiState.page == CreatePostPage.CreatePost)
            navActions.navigateBack()
        else
            addPostViewModel.updatePage(CreatePostPage.CreatePost)
    }

    when (uiState.page) {
        CreatePostPage.CreatePost ->
            CreatePostScreen(
                uiState = uiState,
                onCaptionChanged = addPostViewModel::onCaptionChanged,
                onSelectLocation = addPostViewModel::selectLocation,
                onUploadPost = addPostViewModel::uploadPost,
                interactWithChooseOnMap = { addPostViewModel.updatePage(CreatePostPage.ChooseOnMap) },
                interactWithDrunkennessLevel = { addPostViewModel.updatePage(CreatePostPage.DrunkennessLevel) },
                navigateToTagUser = { addPostViewModel.updatePage(CreatePostPage.AddPeople) },
                navigateToCamera = { navActions.navigateToCamera() },
                unselectLocation = addPostViewModel::unselectLocation,
                updateLocationName = addPostViewModel::updateLocation,
                updateLocationResults = addPostViewModel::updateLocationResults,
                onSelectMedia = addPostViewModel::addPhoto,
                onMediaSelectorClicked = { launcher.launch("image/* video/*") },
                onSelectPrivacy = addPostViewModel::selectPrivacy,
                onNotifyChange = addPostViewModel::toggleNotify,
                onCreatePostUIAction = {
                    when(it) {
                        CreatePostUIAction.OnBackPressed -> navActions.navigateBack()
                        CreatePostUIAction.OnSwipeRefresh -> {}
                    }
                }
            )
        CreatePostPage.ChooseOnMap ->
            ChooseOnMapScreen(
                onSelectLocation = {
                    addPostViewModel.updateLocationPoint(it)
                    addPostViewModel.updatePage(CreatePostPage.CreatePost)
                },
                onBackPressed = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
            )
        CreatePostPage.ChooseBeverage ->
            BeverageScreen(
                onBackPressed = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
            )
        CreatePostPage.AddPeople ->
            AddPeopleScreen(
                onBackPressed = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
                onSelectUser = addPostViewModel::selectTagUser,
                selectedUsers = uiState.selectedTagUsers,
                onDone = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
            )
        CreatePostPage.DrunkennessLevel ->
            DrunkennessLevelScreen(
                onBackPressed = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
                onDone = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
                onSelectDrunkenness = addPostViewModel::onDrunkennessChange,
                drunkenness = uiState.drunkenness,
            )
    }
}