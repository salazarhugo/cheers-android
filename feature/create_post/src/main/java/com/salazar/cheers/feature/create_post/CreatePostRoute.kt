package com.salazar.cheers.feature.create_post

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreatePostRoute(
    navigateBack: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToMoreOptions: () -> Unit,
    navigateToAddDrink: () -> Unit,
    navigateToAddPeople: () -> Unit,
    navigateToAddLocation: () -> Unit,
    navigateToCreateDrink: () -> Unit,
    viewModel: CreatePostViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
        onResult = {
            viewModel.setMedia(context, it)
        },
    )

    CreatePostScreenStateful(
        uiState = uiState,
        onUploadPost = viewModel::uploadPost,
        onCreatePostUIAction = {
            when (it) {
                CreatePostUIAction.OnBackPressed -> navigateBack()
                CreatePostUIAction.OnSwipeRefresh -> {}
                CreatePostUIAction.OnCameraClick -> navigateToCamera()
                CreatePostUIAction.OnAddDrinkClick -> navigateToAddDrink()
                CreatePostUIAction.OnAddPeopleClick -> navigateToAddPeople()
                CreatePostUIAction.OnCreateDrinkClick -> navigateToCreateDrink()
                CreatePostUIAction.OnMoreOptionsClick -> navigateToMoreOptions()
                CreatePostUIAction.OnAudioClick -> viewModel.onAudioClick()
                CreatePostUIAction.OnLocationClick -> navigateToAddLocation()
                CreatePostUIAction.OnGalleryClick -> {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                }

                is CreatePostUIAction.OnCaptionChange -> viewModel.onCaptionChanged(it.text)
                is CreatePostUIAction.OnNotificationChange -> viewModel.toggleNotify(it.enabled)
                is CreatePostUIAction.OnEnableCommentsChange -> viewModel.onEnableCommentsChange(it.enabled)
                is CreatePostUIAction.OnSelectPrivacy -> viewModel.selectPrivacy(it.privacy)
                is CreatePostUIAction.OnSelectDrink -> viewModel.selectDrink(it.drink)
                is CreatePostUIAction.OnAddAudio -> viewModel.addAudio(it.localAudio)
                is CreatePostUIAction.OnSelectLocation -> viewModel.updateLocation(it.location)
                is CreatePostUIAction.OnEnableShareChange -> viewModel.onEnableShareChange(it.enabled)
                is CreatePostUIAction.OnEnableLikesChange -> viewModel.onEnabledLikesChange(it.enabled)
            }
        }
    )
}

//                CreatePostPage . ChooseBeverage ->
//    BeverageScreen(
//        onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
//    )
//
//    CreatePostPage.AddPeople ->
//    AddPeopleScreen(
//        onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
//        onSelectUser = viewModel::selectTagUser,
//        selectedUsers = uiState.selectedTagUsers,
//        onDone = { viewModel.updatePage(CreatePostPage.CreatePost) },
//    )
//
//    CreatePostPage.DrunkennessLevel ->
//    DrunkennessLevelScreen(
//        onBackPressed = { viewModel.updatePage(CreatePostPage.CreatePost) },
//        onDone = { viewModel.updatePage(CreatePostPage.CreatePost) },
//        onSelectDrunkenness = viewModel::onDrunkennessChange,
//        drunkenness = uiState.drunkenness,
//    )
//                onCaptionChanged = viewModel::onCaptionChanged,
//                onSelectLocation = viewModel::selectLocation,
//                interactWithChooseOnMap = { viewModel.updatePage(CreatePostPage.ChooseOnMap) },
//                interactWithDrunkennessLevel = { viewModel.updatePage(CreatePostPage.DrunkennessLevel) },
//                unselectLocation = viewModel::unselectLocation,
//                updateLocationName = viewModel::updateLocation,
//                updateLocationResults = viewModel::updateLocationResults,
//                onSelectMedia = viewModel::addPhoto,
