package com.salazar.cheers.ui.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import coil.compose.rememberImagePainter
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
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.util.Utils

@Composable
fun AddPostScreen(
    uiState: AddPostUiState,
    profilePictureUrl: String,
    onShowOnMapChanged: (showOnMap: Boolean) -> Unit,
    onDismiss: () -> Unit,
    onUploadPost: () -> Unit,
    interactWithChooseOnMap: () -> Unit,
    interactWithChooseBeverage: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToTagUser: () -> Unit,
    onSelectLocation: (SearchResult) -> Unit,
    onSelectMedia: (Uri) -> Unit,
    onCaptionChanged: (String) -> Unit,
    unselectLocation: () -> Unit,
    updateLocationName: (String) -> Unit,
    updateLocationResults: (List<SearchResult>) -> Unit,
    onMediaSelectorClicked: () -> Unit,
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
        val reverseGeocoding = remember { MapboxSearchSdk.getReverseGeocodingSearchEngine() }
        reverseGeocoding.search(options, searchCallback)
    }

    Scaffold(
        topBar = { TopAppBar(onDismiss = onDismiss) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AddPhotoOrVideo(
                mediaUri = uiState.mediaUri,
                navigateToCamera = navigateToCamera,
                onSelectMedia = onSelectMedia,
                onMediaSelectorClicked = onMediaSelectorClicked,
            )
//                    DividerM3()
            CaptionSection(
                profilePictureUrl = profilePictureUrl,
                caption = uiState.caption,
                onCaptionChanged = onCaptionChanged,
                mediaUri = uiState.mediaUri,
                postType = uiState.postType,
            )
            DividerM3()
            TagSection(
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
            DividerM3()
            LocationResultsSection(
                results = uiState.locationResults,
                onSelectLocation = onSelectLocation,
            )
            BeverageSection(
                beverage = uiState.beverage,
                interactWithChooseBeverage = interactWithChooseBeverage
            )
            DividerM3()
            SwitchPreference(
                text = "Show on map",
                showOnMap = uiState.showOnMap,
            ) { onShowOnMapChanged(it) }
            DividerM3()
            SwitchPreference(
                text = "Allow repost",
                showOnMap = uiState.showOnMap,
            ) {}
            ShareButton(onDismiss, onUploadPost = onUploadPost)
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
        title = { Text("New post", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "")
            }
        },
    )
}

@Composable
fun ShareButton(
    onDismiss: () -> Unit,
    onUploadPost: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        DividerM3()
        Button(
            onClick = {
                onUploadPost()
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Share")
        }
    }
}

@Composable
fun SwitchPreference(
    showOnMap: Boolean,
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
        SwitchM3(
            checked = showOnMap,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun AddPhotoOrVideo(
    mediaUri: Uri?,
    navigateToCamera: () -> Unit,
    onSelectMedia: (Uri) -> Unit,
    onMediaSelectorClicked: () -> Unit,
) {
    val context = LocalContext.current
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if (it != null) {
                val uri = Utils.getImageUri(context, it)
                if (uri != null)
                    onSelectMedia(uri)
            }
        }
    if (mediaUri != null)
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
            Text(text = "Add from gallery")
        }
        Spacer(Modifier.width(8.dp))
        FilledTonalButton(
            onClick = { takePictureLauncher.launch() },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.PhotoCamera, "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Take a photo")
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
            text = "Location",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.MyLocation, null)
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
        unselectedColor = MaterialTheme.colorScheme.outline,
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
fun TagSection(
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
            text = "Add people",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
        )
        if (selectedTagUsers.size == 1)
            Text(text = selectedTagUsers[0].username, style = MaterialTheme.typography.labelLarge)
        if (selectedTagUsers.size > 1)
            Text(
                text = "${selectedTagUsers.size} people",
                style = MaterialTheme.typography.labelLarge
            )
    }
}

@Composable
fun CaptionSection(
    profilePictureUrl: String,
    mediaUri: Uri?,
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
        val videoUri = mediaUri

        if (videoUri != null && postType == PostType.VIDEO)
            VideoPlayer(
                uri = videoUri,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .height(120.dp)
                    .aspectRatio(9f / 16f)
            )
        else
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
                val photoUri = mediaUri
                if (photoUri != null) {
                    Image(
                        modifier = Modifier
                            .clickable(onClick = { openPhotoVideoChooser() })
                            .padding(horizontal = 16.dp)
                            .size(50.dp),
                        painter = rememberImagePainter(data = mediaUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
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
        painter = rememberImagePainter(
            data = profilePictureUrl,
            builder = {
                transformations(CircleCropTransformation())
                error(R.drawable.default_profile_picture)
            },
        ),
        contentDescription = "Profile image",
        modifier = Modifier
            .clip(CircleShape)
            .size(40.dp),
        contentScale = ContentScale.Crop,
    )
}
