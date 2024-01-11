package com.salazar.cheers.feature.create_post

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Stateful composable that displays the Navigation route for the Add post screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CreatePostRoute(
    navigateBack: () -> Unit,
    navigateToCamera: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
            onResult = viewModel::setPhotos,
        )

    BackHandler {
        if (uiState.page == CreatePostPage.CreatePost)
            navigateBack()
        else
            viewModel.updatePage(CreatePostPage.CreatePost)
    }

    when (uiState.page) {
        CreatePostPage.CreatePost ->
            CreatePostScreenStateful(
                uiState = uiState,
                onCaptionChanged = viewModel::onCaptionChanged,
//                onSelectLocation = viewModel::selectLocation,
                onUploadPost = viewModel::uploadPost,
                interactWithChooseOnMap = { viewModel.updatePage(CreatePostPage.ChooseOnMap) },
                interactWithDrunkennessLevel = { viewModel.updatePage(CreatePostPage.DrunkennessLevel) },
                navigateToTagUser = { viewModel.updatePage(CreatePostPage.AddPeople) },
                unselectLocation = viewModel::unselectLocation,
                updateLocationName = viewModel::updateLocation,
//                updateLocationResults = viewModel::updateLocationResults,
                onSelectMedia = viewModel::addPhoto,
                onSelectPrivacy = viewModel::selectPrivacy,
                onCreatePostUIAction = {
                    when(it) {
                        CreatePostUIAction.OnBackPressed -> navigateBack()
                        CreatePostUIAction.OnSwipeRefresh -> {}
                        CreatePostUIAction.OnCameraClick -> navigateToCamera()
                        is CreatePostUIAction.OnCaptionChange -> viewModel.onCaptionChanged(it.text)
                        CreatePostUIAction.OnGalleryClick -> {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        }
                        is CreatePostUIAction.OnNotificationChange -> viewModel.toggleNotify(it.enabled)
                    }
                }
            )
        CreatePostPage.ChooseOnMap -> {}
//            ChooseOnMapScreen(
//                onSelectLocation = {
//                    addPostViewModel.updateLocationPoint(it)
//                    addPostViewModel.updatePage(CreatePostPage.CreatePost)
//                },
//                onBackPressed = { addPostViewModel.updatePage(CreatePostPage.CreatePost) },
//            )
        CreatePostPage.ChooseBeverage ->
            BeverageScreen(
                onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
            )
        CreatePostPage.AddPeople ->
            AddPeopleScreen(
                onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
                onSelectUser = viewModel::selectTagUser,
                selectedUsers = uiState.selectedTagUsers,
                onDone = { viewModel.updatePage(CreatePostPage.CreatePost) },
            )
        CreatePostPage.DrunkennessLevel ->
            DrunkennessLevelScreen(
                onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
                onDone = { viewModel.updatePage(CreatePostPage.CreatePost) },
                onSelectDrunkenness = viewModel::onDrunkennessChange,
                drunkenness = uiState.drunkenness,
            )
    }
}