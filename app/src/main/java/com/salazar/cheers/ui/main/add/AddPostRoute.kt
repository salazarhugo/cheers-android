package com.salazar.cheers.ui.main.add

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.components.utils.BackPressHandler
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.main.map.ChooseOnMapScreen

/**
 * Stateful composable that displays the Navigation route for the Add post screen.
 *
 * @param addPostViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun AddPostRoute(
    profilePictureUrl: String,
    navActions: CheersNavigationActions,
    addPostViewModel: AddPostViewModel = hiltViewModel(),
) {
    val uiState by addPostViewModel.uiState.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            if (it.size <= 8)
                addPostViewModel.setPhotos(it)
            else
                addPostViewModel.updateErrorMessage("Maximum 8 photos.")
        }

    BackHandler {
        addPostViewModel.updatePage(AddPostPage.AddPost)
        Log.d("HAHA", "System back intercepted")
        println("System back intercepted")
    }
    when (uiState.page) {
        AddPostPage.AddPost ->
            AddPostScreen(
                uiState = uiState,
                profilePictureUrl = profilePictureUrl,
                onCaptionChanged = addPostViewModel::onCaptionChanged,
                onSelectLocation = addPostViewModel::selectLocation,
                onUploadPost = addPostViewModel::uploadPost,
                onDismiss = navActions.navigateBack,
                interactWithChooseOnMap = { addPostViewModel.updatePage(AddPostPage.ChooseOnMap) },
                interactWithChooseBeverage = { addPostViewModel.updatePage(AddPostPage.ChooseBeverage) },
                interactWithDrunkennessLevel = { addPostViewModel.updatePage(AddPostPage.DrunkennessLevel) },
                navigateToTagUser = { addPostViewModel.updatePage(AddPostPage.AddPeople) },
                navigateToCamera = { navActions.navigateToCamera() },
                unselectLocation = addPostViewModel::unselectLocation,
                updateLocationName = addPostViewModel::updateLocation,
                updateLocationResults = addPostViewModel::updateLocationResults,
                onSelectMedia = addPostViewModel::addPhoto,
                onMediaSelectorClicked = { launcher.launch("image/* video/*") },
                onSelectPrivacy = addPostViewModel::selectPrivacy,
                onAllowJoinChange = addPostViewModel::toggleAllowJoin
            )
        AddPostPage.ChooseOnMap ->
            ChooseOnMapScreen(
                onSelectLocation = {
                    addPostViewModel.updateLocationPoint(it)
                    addPostViewModel.updatePage(AddPostPage.AddPost)
                },
                onBackPressed = { addPostViewModel.updatePage(AddPostPage.AddPost) },
            )
        AddPostPage.ChooseBeverage ->
            BeverageScreen(
                onBackPressed = { addPostViewModel.updatePage(AddPostPage.AddPost) },
                onSelectBeverage = {
                    addPostViewModel.onSelectBeverage(it)
                    addPostViewModel.updatePage(AddPostPage.AddPost)
                },
            )
        AddPostPage.AddPeople ->
            AddPeopleScreen(
                onBackPressed = { addPostViewModel.updatePage(AddPostPage.AddPost) },
                onSelectUser = addPostViewModel::selectTagUser,
                selectedUsers = uiState.selectedTagUsers,
                onDone = { addPostViewModel.updatePage(AddPostPage.AddPost) },
            )
        AddPostPage.DrunkennessLevel ->
            DrunkennessLevelScreen(
                onBackPressed = { addPostViewModel.updatePage(AddPostPage.AddPost) },
                onDone = { addPostViewModel.updatePage(AddPostPage.AddPost) },
                onSelectDrunkenness = addPostViewModel::onDrunkennessChange,
                drunkenness = uiState.drunkenness,
            )
    }
}