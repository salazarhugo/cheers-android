package com.salazar.cheers.feature.create_post

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.ChipGroup
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.playback.PlaybackComponent
import com.salazar.cheers.core.ui.components.post.PostMedia
import com.salazar.cheers.core.ui.components.post.PostDrink
import com.salazar.cheers.core.ui.components.post.PostHeader
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.ErrorMessage
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.feature.create_post.drink.SelectDrinkBottomSheet
import com.salazar.common.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun CreatePostScreenStateful(
    uiState: CreatePostUiState,
    onUploadPost: () -> Unit,
    navigateToTagUser: () -> Unit,
//    onSelectLocation: (SearchResult) -> Unit,
//    updateLocationResults: (List<SearchResult>) -> Unit,
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

    CreatePostScreen(
        account = uiState.account,
        audio = uiState.audio,
        caption = uiState.caption,
        privacy = uiState.privacy,
        errorMessage = uiState.errorMessage,
        selectedDrink = uiState.currentDrink,
        onCreatePostUIAction = onCreatePostUIAction,
        drinks = uiState.drinks,
        isAudioPlaying = uiState.isAudioPlaying,
        audioProgress = uiState.audioProgress,
        onUploadPost = onUploadPost,
        medias = uiState.medias,
        notificationEnabled = uiState.notify,
        isLoading = uiState.isLoading,
        locationResults = uiState.locationResults,
//        location = uiState.location,
    )
}

@Composable
fun CreatePostScreen(
    caption: String,
    privacy: Privacy,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    selectedDrink: Drink? = null,
    locationResults: List<String>? = null,
    audio: LocalAudio? = null,
    isAudioPlaying: Boolean = false,
    audioProgress: Float = 0f,
    account: Account? = null,
    medias: List<Media> = emptyList(),
    drinks: List<Drink> = emptyList(),
    notificationEnabled: Boolean = true,
    isLoading: Boolean = false,
    onUploadPost: () -> Unit = {},
    onCreatePostUIAction: (CreatePostUIAction) -> Unit = {},
) {
    val enabled = caption.isNotEmpty() || medias.isNotEmpty() // || uiState.drinkState.currentPage > 0
    val drinkState = rememberPagerState {
        drinks.size
    }
    var showSelectDrinkSheet by remember { mutableStateOf(false) }
    var showAudioRecorderDialog by remember { mutableStateOf(false) }
    val drinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val audioSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            CreatePostTopBar(
                onDismiss = { onCreatePostUIAction(CreatePostUIAction.OnBackPressed) },
                onShare = {
                    onUploadPost()
                    onCreatePostUIAction(CreatePostUIAction.OnBackPressed)
                },
                isLoading = isLoading,
            )
        },
        bottomBar = {
            CreatePostBottomBar(
                privacy = privacy,
                modifier = Modifier
                    .navigationBarsPadding()
                    .background(MaterialTheme.colorScheme.background)
                    .shadow(8.dp, spotColor = Color.Transparent)
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                onClick = {
                    onUploadPost()
                    onCreatePostUIAction(CreatePostUIAction.OnBackPressed)
                },
                onSelectPrivacy = {
                    onCreatePostUIAction(CreatePostUIAction.OnSelectPrivacy(it))
                },
                enabled = enabled,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ErrorMessage(
                errorMessage = errorMessage,
                paddingValues = PaddingValues(16.dp),
            )
            if (account != null) {
                PostHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    username = account.username,
                    verified = account.verified,
                    avatar = account.picture,
                )
            }
            CaptionSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                caption = caption,
                onCaptionChanged = {
                    onCreatePostUIAction(CreatePostUIAction.OnCaptionChange(it))
                },
            )
            PostMedia(
                medias = medias,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (audio != null) {
                PlaybackComponent(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(16.dp),
                    isPlaying = isAudioPlaying,
                    progress = audioProgress,
                    amplitudes = audio.amplitudes,
                    onClick = {
                        onCreatePostUIAction(CreatePostUIAction.OnAudioClick)
                    }
                )
            }
            CreatePostButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onAddImageClick = {
                    onCreatePostUIAction(CreatePostUIAction.OnGalleryClick)
                },
                onAddDrinkClick = {
                    scope.launch {
                        showSelectDrinkSheet = true
                        drinkSheetState.expand()
                    }
                },
                onMicrophoneClick = {
                    scope.launch {
                        showAudioRecorderDialog = true
                        audioSheetState.expand()
                    }
                }
            )
            if (selectedDrink != null) {
                PostDrink(
                    drink = selectedDrink.name,
                    picture = selectedDrink.icon,
                    modifier = Modifier.padding(16.dp),
                )
            }
            HorizontalDivider()
            EndDateSection(
                endDate = Date(),
                onEndDateChange = {},
            )
            HorizontalDivider()
            LocationSection(
                location = "",
                navigateToChooseOnMap = {
                    onCreatePostUIAction(CreatePostUIAction.OnLocationClick)
                },
            )
            if (locationResults != null) {
                LocationResultsSection(
                    results = locationResults,
                    onSelectLocation = {
                    },
                )
            }
            HorizontalDivider()
            SwitchPreference(
                text = "Send notification to friends",
                checked = notificationEnabled,
                onCheckedChange = {
                    onCreatePostUIAction(CreatePostUIAction.OnNotificationChange(it))
                },
            )
        }
    }

    if (showAudioRecorderDialog) {
        AudioRecorderDialog(
            sheetState = audioSheetState,
            onDismiss = {
                scope.launch {
                    audioSheetState.hide()
                }.invokeOnCompletion {
                    showAudioRecorderDialog = false
                }
            },
            onDone = {
                onCreatePostUIAction(CreatePostUIAction.OnAddAudio(it))
            }
        )
    }

    if (showSelectDrinkSheet) {
        SelectDrinkBottomSheet(
            drinks = drinks,
            sheetState = drinkSheetState,
            onClick = { drink ->
                onCreatePostUIAction(CreatePostUIAction.OnSelectDrink(drink))
            },
            onDismiss = {
                scope.launch {
                    drinkSheetState.hide()
                }.invokeOnCompletion {
                    showSelectDrinkSheet = false
                }
            },
        )
    }
}

@ScreenPreviews
@Composable
private fun CreatePostScreenPreview() {
    CheersPreview {
        CreatePostScreen(
            caption = "Geneva is one of the most famous city in the world.",
            modifier = Modifier,
            medias = List(4) {
                Media.Image(uri = Uri.parse(""))
             },
            account = Account(
                username = "cheers",
                verified = true,
                name = "Cheers Social",
            ),
            privacy = Privacy.FRIENDS,
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
fun PrivacyComponent(
    privacy: Privacy,
    onSelectPrivacy: (Privacy) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var showPrivacySheet by remember { mutableStateOf(false) }
    val state = rememberModalBottomSheetState()

    if (showPrivacySheet) {
        PrivacyBottomSheet(
            privacy = privacy,
            privacyState = state,
            onSelectPrivacy = onSelectPrivacy,
            onDismiss = {
                scope.launch {
                    state.hide()
                }.invokeOnCompletion {
                    showPrivacySheet = false
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .noRippleClickable {
                scope.launch {
                    showPrivacySheet = true
                    state.show()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = privacy.icon,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(
                text = privacy.subtitle,
            )
        }
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
fun CreatePostTopBar(
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
        actions = {}
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
    caption: String,
    modifier: Modifier = Modifier,
    onCaptionChanged: (String) -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = caption,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth(),
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
        )
    }
}

