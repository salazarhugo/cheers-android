package com.salazar.cheers.ui.main.add

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.post.MultipleAnnotation
import com.salazar.cheers.components.share.ErrorMessage
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.event.add.Item
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddPostScreen(
    uiState: AddPostUiState,
    profilePictureUrl: String,
    onDismiss: () -> Unit,
    onUploadPost: () -> Unit,
    interactWithChooseOnMap: () -> Unit,
    interactWithChooseBeverage: () -> Unit,
    interactWithDrunkennessLevel: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToTagUser: () -> Unit,
    onSelectLocation: (SearchResult) -> Unit,
    onSelectMedia: (Uri) -> Unit,
    onCaptionChanged: (String) -> Unit,
    onNotifyChange: (Boolean) -> Unit,
    unselectLocation: () -> Unit,
    updateLocationName: (String) -> Unit,
    updateLocationResults: (List<SearchResult>) -> Unit,
    onMediaSelectorClicked: () -> Unit,
    onSelectPrivacy: (Privacy) -> Unit,
) {
    val searchCallback = object : SearchCallback {
        override fun onResults(
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            if (results.isNotEmpty()) {
                updateLocationResults(results)
                updateLocationName("On Pin")
            }
        }

        override fun onError(e: Exception) {}
    }

    if (uiState.locationPoint != null) {
        val options = ReverseGeoOptions(
            center = uiState.locationPoint,
        )
        val reverseGeocoding = remember { MapboxSearchSdk.getSearchEngine() }
        reverseGeocoding.search(options, searchCallback)
    }

    PrivacyBottomSheet(
        privacy = uiState.privacy,
        privacyState = uiState.privacyState,
        onSelectPrivacy = onSelectPrivacy,
    ) {
        Scaffold(
            topBar = { TopAppBar(onDismiss = onDismiss) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                AddPhotoOrVideo(
                    photos = uiState.photos,
                    navigateToCamera = navigateToCamera,
                    onSelectMedia = onSelectMedia,
                    onMediaSelectorClicked = onMediaSelectorClicked,
                )
                ErrorMessage(
                    errorMessage = uiState.errorMessage,
                    paddingValues = PaddingValues(16.dp),
                )
                CaptionSection(
                    profilePictureUrl = profilePictureUrl,
                    caption = uiState.caption,
                    onCaptionChanged = onCaptionChanged,
                    photos = uiState.photos,
                    postType = uiState.postType,
                )
                DividerM3()
                AddPeople(
                    selectedTagUsers = uiState.selectedTagUsers,
                    navigateToTagUser = navigateToTagUser,
                )
                DividerM3()
                if (uiState.selectedLocation != null)
                    SelectedLocation(
                        location = uiState.selectedLocation,
                        navigateToChooseOnMap = interactWithChooseOnMap,
                        unselectLocation = unselectLocation,
                    )
                else
                    LocationSection(
                        location = uiState.location,
                        navigateToChooseOnMap = interactWithChooseOnMap
                    )
                LocationResultsSection(
                    results = uiState.locationResults,
                    onSelectLocation = onSelectLocation,
                )
                DividerM3()
                BeverageSection(
                    beverage = uiState.beverage.displayName,
                    interactWithChooseBeverage = interactWithChooseBeverage
                )
                DividerM3()
                DrunkennessLevelSection(
                    drunkenness = uiState.drunkenness,
                    interactWithChooseBeverage = interactWithDrunkennessLevel
                )
                DividerM3()
                Privacy(
                    privacyState = uiState.privacyState,
                    privacy = uiState.privacy,
                )
                DividerM3()
                EndDateSection(
                    endDate = Date(),
                    onEndDateChange = {},
                )
                DividerM3()
                SwitchPreference(
                    text = "Notify followers",
                    checked = uiState.notify,
                    onCheckedChange = onNotifyChange
                )
                ShareButton("Share", onClick = {
                    onUploadPost()
                    onDismiss()
                })
            }
        }
    }
}

@Composable
fun EndDateSection(
    endDate: Date,
    onEndDateChange: (Date) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Map timeout",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "24 hours",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
            )
        }
    }
}

@Composable
fun Privacy(
    privacyState: ModalBottomSheetState,
    privacy: Privacy,
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch { privacyState.show() }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(privacy.icon, null)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(privacy.title)
                Text(privacy.subtitle)
            }
        }
        Icon(Icons.Filled.KeyboardArrowRight, null)
    }
}

@Composable
fun NameSection(
    name: String,
    onNameChanged: (String) -> Unit,
) {
    TextField(
        value = name,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 4.dp),
        onValueChange = { onNameChanged(it) },
        singleLine = false,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        placeholder = {
            Text(text = "Name", fontSize = 13.sp)
        },
        trailingIcon = { }
    )
}

@Composable
fun DrunkennessLevelSection(
    drunkenness: Int,
    interactWithChooseBeverage: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { interactWithChooseBeverage() }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Drunkenness Level",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Bloodtype, null)
            if (drunkenness > 0)
                Text(
                    text = drunkenness.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
        }
    }
}

@Composable
fun BeverageSection(
    beverage: String,
    interactWithChooseBeverage: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { interactWithChooseBeverage() }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Add beverage",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.LocalBar, null)
            if (beverage.isNotBlank())
                Text(
                    text = beverage,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
        }
    }
}

@Composable
fun TopAppBar(
    onDismiss: () -> Unit
) {
    SmallTopAppBar(
        title = {
            Text("New post", fontWeight = FontWeight.Bold, fontFamily = Roboto)
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "")
            }
        },
    )
}

@Composable
fun ShareButton(
    text: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        DividerM3()
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun SwitchPreference(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
fun AddPhotoOrVideo(
    photos: List<Uri>,
    navigateToCamera: () -> Unit,
    onSelectMedia: (Uri) -> Unit,
    onMediaSelectorClicked: () -> Unit,
) {
    if (photos.isNotEmpty())
        return
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        FilledTonalButton(
            onClick = onMediaSelectorClicked,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.PhotoAlbum, "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Gallery")
        }
        Spacer(Modifier.width(8.dp))
        FilledTonalButton(
            onClick = {
                navigateToCamera()
//                takePictureLauncher.launch()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.PhotoCamera, "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Take photo")
        }
    }
}

@Composable
fun LocationSection(
    location: String,
    navigateToChooseOnMap: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { navigateToChooseOnMap() }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Add location",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.MyLocation, null)
            if (location.isNotBlank())
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
        }
    }
}

@Composable
fun LocationResultsSection(
    results: List<SearchResult>,
    onSelectLocation: (SearchResult) -> Unit,
) {
    if (results.isNotEmpty())
        LocationResult(results = results, onSelectLocation = onSelectLocation)
}

@Composable
fun LocationResult(
    results: List<SearchResult>,
    onSelectLocation: (SearchResult) -> Unit,
) {
    ChipGroup(
        users = results.map { it.name },
        onSelectedChanged = { name ->
            val location = results.find { it.name == name }
            if (location != null)
                onSelectLocation(location)
        },
    )
}

@Composable
fun SelectedLocation(
    location: SearchResult,
    navigateToChooseOnMap: () -> Unit,
    unselectLocation: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { navigateToChooseOnMap() }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.tertiary)
            Text(text = location.name, fontSize = 14.sp)
        }
        Icon(
            Icons.Outlined.Close,
            null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.clickable { unselectLocation() }
        )
    }
}

@Composable
fun AddPeople(
    selectedTagUsers: List<User>,
    navigateToTagUser: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { navigateToTagUser() }
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Add friends",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        if (selectedTagUsers.size == 1)
            Text(text = selectedTagUsers[0].username, style = MaterialTheme.typography.labelLarge)
        else if (selectedTagUsers.size > 1)
            Text(
                text = "${selectedTagUsers.size} friends",
                style = MaterialTheme.typography.labelLarge
            )
        else
            Icon(Icons.Outlined.People, contentDescription = null)
    }
}

@Composable
fun CaptionSection(
    profilePictureUrl: String,
    photos: List<Uri>,
    postType: String,
    caption: String,
    onCaptionChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val videoUri = photos

//        if (videoUri.isNotEmpty() && postType == PostType.VIDEO)
//            VideoPlayer(
//                uri = videoUri,
//                modifier = Modifier
//                    .padding(bottom = 8.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .height(120.dp)
//                    .aspectRatio(9f / 16f)
//            )
//        else
        ProfilePicture(profilePictureUrl = profilePictureUrl)

        TextField(
            value = caption,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 4.dp),
            onValueChange = { onCaptionChanged(it) },
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            placeholder = {
                Text(text = "Write a caption...", fontSize = 13.sp)
            },
            trailingIcon = {
                if (photos.isEmpty()) return@TextField
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Image(
                        modifier = Modifier
                            .clickable(onClick = { openPhotoVideoChooser() })
                            .size(50.dp),
                        painter = rememberAsyncImagePainter(model = photos[0]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    if (photos.size > 1)
                        MultipleAnnotation(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(0.dp)
                        )
                }
            }
        )

    }
}

private fun openPhotoVideoChooser() {
//    Utils.openPhotoVideoChooser(singleImageResultLauncher)
}

private fun singleImageResultLauncher(
    onSetPostImage: (Uri) -> Unit,
    onSetPostVideo: (Uri) -> Unit,
) {
//    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val imageOrVideoUri: Uri = data?.data ?: return@registerForActivityResult
//
//            val type = data.resolveType(requireContext()) ?: ""
//            if (type.startsWith("image")) {
//                onSetPostImage(imageOrVideoUri)
//            } else if (type.startsWith("video"))
//                onSetPostVideo(imageOrVideoUri)
//        }
//    }
}

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Create media item
    val mediaItem = MediaItem.fromUri(uri)

    // Create the player
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(mediaItem)
            this.prepare()
            this.playWhenReady = true
            this.repeatMode = Player.REPEAT_MODE_ALL
            this.volume = 0f
            this.videoScalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                }
            },
            modifier = modifier
        ) {
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        }
    ) {
        onDispose {
            player.release()
        }
    }
}

@Composable
fun ProfilePicture(profilePictureUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = profilePictureUrl)
                .apply(block = fun ImageRequest.Builder.() {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                }).build()
        ),
        contentDescription = "Profile image",
        modifier = Modifier
            .clip(CircleShape)
            .size(40.dp),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun PrivacyBottomSheet(
    privacy: Privacy,
    privacyState: ModalBottomSheetState,
    onSelectPrivacy: (Privacy) -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = privacyState,
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        sheetElevation = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline)
                )
                Text(
                    "Event privacy",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
                DividerM3()
                Text(
                    "Choose who can see and join this event. You'll be able to invite people later.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Privacy.values().forEach {
                Item(it, it == privacy, onSelectPrivacy = {
                    onSelectPrivacy(it)
                    scope.launch {
                        privacyState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                })
            }

            Button(
                onClick = {
                    scope.launch {
                        privacyState.hide()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Done")
            }
        }
    ) {
        content()
    }
}
