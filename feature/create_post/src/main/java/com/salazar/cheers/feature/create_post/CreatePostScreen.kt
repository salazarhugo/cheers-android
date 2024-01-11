package com.salazar.cheers.feature.create_post

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CarouselDrinks
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.ChipGroup
import com.salazar.cheers.core.ui.MultipleAnnotation
import com.salazar.cheers.core.ui.ShareButton
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.GreySheet
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.ErrorMessage
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun CreatePostScreenStateful(
    uiState: CreatePostUiState,
    onUploadPost: (Int) -> Unit,
    interactWithChooseOnMap: () -> Unit,
    interactWithDrunkennessLevel: () -> Unit,
    navigateToTagUser: () -> Unit,
//    onSelectLocation: (SearchResult) -> Unit,
    onSelectMedia: (Uri) -> Unit,
    onCaptionChanged: (String) -> Unit,
    unselectLocation: () -> Unit,
    updateLocationName: (String) -> Unit,
//    updateLocationResults: (List<SearchResult>) -> Unit,
    onSelectPrivacy: (Privacy) -> Unit,
    onCreatePostUIAction: (CreatePostUIAction) -> Unit,
) {
//    val searchCallback = object : SearchCallback {
//        override fun onResults(
//            results: List<SearchResult>,
//            responseInfo: ResponseInfo
//        ) {
//            if (results.isNotEmpty()) {
//                updateLocationResults(results)
//                updateLocationName("On Pin")
//            }
//        }
//
//        override fun onError(e: Exception) {}
//    }

    if (uiState.locationPoint != null) {
//        val options = ReverseGeoOptions(
//            center = uiState.locationPoint,
//        )
        val context = LocalContext.current
//        val reverseGeocoding = remember {
//            SearchEngine.createSearchEngine(
//                SearchEngineSettings(context.getString(R.string.mapbox_access_token))
//            )
//        }
//        reverseGeocoding.search(options, searchCallback)
    }

    if (uiState.privacyState.isVisible)
        PrivacyBottomSheet(
            privacy = uiState.privacy,
            privacyState = uiState.privacyState,
            onSelectPrivacy = onSelectPrivacy,
        )

    CreatePostScreen(
        avatar = uiState.profilePictureUrl,
        caption = uiState.caption,
        errorMessage = uiState.errorMessage,
        onCreatePostUIAction = onCreatePostUIAction,
        location = uiState.location,
//        locationResults = uiState.locationResults.map { it.name },
        drinks = uiState.drinks,
        onUploadPost = onUploadPost,
        photos = uiState.photos,
        notificationEnabled = uiState.notify,
        isLoading = uiState.isLoading,
    )
}

@Composable
fun CreatePostScreen(
    caption: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    location: String? = null,
    locationResults: List<String> = emptyList(),
    avatar: String? = null,
    photos: List<Uri> = emptyList(),
    drinks: List<Drink> = emptyList(),
    notificationEnabled: Boolean = true,
    isLoading: Boolean = false,
    onUploadPost: (Int) -> Unit = {},
    onCreatePostUIAction: (CreatePostUIAction) -> Unit = {},
) {
    val enabled = caption.isNotEmpty() || photos.isNotEmpty() // || uiState.drinkState.currentPage > 0
    val drinkState = rememberPagerState {
        drinks.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onDismiss = { onCreatePostUIAction(CreatePostUIAction.OnBackPressed) },
                onShare = {
                    val drinkID = drinks[drinkState.currentPage]
                    onUploadPost(drinkID.id)
                    onCreatePostUIAction(CreatePostUIAction.OnBackPressed)
                },
                isLoading = isLoading,
            )
        },
        bottomBar = {
            ShareButton(
                modifier = Modifier.navigationBarsPadding(),
                text = stringResource(id = R.string.share),
                isLoading = isLoading,
                onClick = {
                    val drinkID = drinks[drinkState.currentPage]
                    onUploadPost(drinkID.id)
                    onCreatePostUIAction(CreatePostUIAction.OnBackPressed)
                },
                enabled = enabled,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            AnimatedVisibility(visible = photos.isEmpty()) {
                AddPhotoOrVideo(
                    navigateToCamera = {
                        onCreatePostUIAction(CreatePostUIAction.OnCameraClick)
                    },
                    onMediaSelectorClicked = {
                        onCreatePostUIAction(CreatePostUIAction.OnGalleryClick)
                    },
                )
            }
            ErrorMessage(
                errorMessage = errorMessage,
                paddingValues = PaddingValues(16.dp),
            )
            CaptionSection(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .fillMaxWidth(),
                avatar = avatar,
                caption = caption,
                onCaptionChanged = {
                    onCreatePostUIAction(CreatePostUIAction.OnCaptionChange(it))
                },
                photos = photos,
                onImageClick = {
                    onCreatePostUIAction(CreatePostUIAction.OnGalleryClick)
                },
            )
            Divider()
            AddPeople(
                users = emptyList(),
                navigateToTagUser = {},
            )
            Divider()
            if (location != null)
                SelectedLocation(
                    location = location,
                    navigateToChooseOnMap = {},//interactWithChooseOnMap,
                    unselectLocation = {}, //unselectLocation,
                )
            else
                LocationSection(
                    location = location,
                    navigateToChooseOnMap = {},
                )
            LocationResultsSection(
                results = locationResults,
                onSelectLocation = {},
            )
            Divider()
            BeverageSection(
                pagerState = drinkState,
                drinks = drinks,
                onCreatePostUIAction = onCreatePostUIAction,
            )
//                Privacy(
//                    privacyState = privacyState,
//                    privacy = privacy,
//                )
            Divider()
            EndDateSection(
                endDate = Date(),
                onEndDateChange = {},
            )
            Divider()
            SwitchPreference(
                text = "Send notification to friends",
                checked = notificationEnabled,
                onCheckedChange = {
                    onCreatePostUIAction(CreatePostUIAction.OnNotificationChange(it))
                },
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun CreatePostScreenPreview() {
    CheersPreview {
        CreatePostScreen(
            caption = "Geneva is one of the most famous city in the world.",
        )
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
    onClick: () -> Unit,
    privacy: Privacy,
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch { onClick() }
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
        colors = TextFieldDefaults.colors(
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
    pagerState: PagerState,
    drinks: List<Drink>,
    onCreatePostUIAction: (CreatePostUIAction) -> Unit,
) {
    CarouselDrinks(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        pagerState = pagerState,
        drinks = drinks,
        onBeverageClick = { },
    )
}

@Composable
fun TopAppBar(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    isLoading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "New post",
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(Icons.Default.Close, null)
            }
        },
        actions = {
            if (isLoading)
                CircularProgressIndicator()
            else
                TextButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = onShare,
                ) {
                    Text(
                        text = stringResource(id = R.string.share),
                    )
                }
        }
    )
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
            thumbContent = {
                when (checked) {
                    true -> Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(androidx.compose.material3.SwitchDefaults.IconSize),
                    )

                    false -> Unit
                }
            }
        )
    }
}

@Composable
fun AddPhotoOrVideo(
    navigateToCamera: () -> Unit,
    onMediaSelectorClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        FilledTonalButton(
            onClick = onMediaSelectorClicked,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Outlined.PhotoAlbum, null)
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
    location: String?,
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
            if (location?.isNotBlank() == true)
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
        }
    }
}

@Composable
fun LocationResultsSection(
    results: List<String>,
    onSelectLocation: (String) -> Unit,
) {
    if (results.isNotEmpty()) {
        LocationResult(
            results = results,
            onSelectLocation = onSelectLocation,
        )
    }
}

@Composable
fun LocationResult(
    results: List<String>,
    onSelectLocation: (String) -> Unit,
) {
    ChipGroup(
        users = results,
        onSelectedChanged = { name ->
            val location = results.find { it == name }
            if (location != null)
                onSelectLocation(location)
        },
    )
}

@Composable
fun SelectedLocation(
    location: String,
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
            Text(text = location, fontSize = 14.sp)
        }
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.clickable { unselectLocation() }
        )
    }
}

@Composable
fun AddPeople(
    users: List<com.salazar.cheers.core.model.UserItem>,
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
        if (users.size == 1)
            Text(text = users[0].username, style = MaterialTheme.typography.labelLarge)
        else if (users.size > 1)
            Text(
                text = "${users.size} friends",
                style = MaterialTheme.typography.labelLarge
            )
        else
            Icon(Icons.Outlined.People, contentDescription = null)
    }
}

@Composable
fun CaptionSection(
    modifier: Modifier = Modifier,
    avatar: String?,
    photos: List<Uri>,
    caption: String,
    onCaptionChanged: (String) -> Unit,
    onImageClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarComponent(
            avatar = avatar,
            size = 40.dp,
        )

        TextField(
            value = caption,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
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
                            .clickable(onClick = onImageClick)
                            .size(50.dp),
                        painter = rememberAsyncImagePainter(model = photos[0]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    if (photos.size > 1)
                        MultipleAnnotation(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(0.dp),
                            onClick = onImageClick,
                        )
                }
            }
        )
    }
}

@Composable
fun PrivacyBottomSheet(
    privacy: Privacy,
    privacyState: SheetState,
    onSelectPrivacy: (Privacy) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = privacyState,
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = {},
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    "Event privacy",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
                Divider()
                Text(
                    "Choose who can see and join this event. You'll be able to invite people later.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Privacy.values().forEach {
//                Item(it, it == privacy, onSelectPrivacy = {
//                    onSelectPrivacy(it)
//                    scope.launch {
//                        privacyState.show()
//                    }
//                })
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
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Done")
            }
        }
    )
}
