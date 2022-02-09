package com.salazar.cheers.ui.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeocodingSearchEngine
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch
import java.util.*


private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
private lateinit var searchRequestTask: SearchRequestTask

private val searchCallback = object : SearchCallback {

    override fun onResults(
        results: List<SearchResult>,
        responseInfo: ResponseInfo
    ) {
        if (results.isEmpty()) {
            Log.i("SearchApiExample", "No reverse geocoding results")
        } else {
            Log.i("SearchApiExample", "Reverse geocoding results: $results")
//            updateLocationResults(results)
//            updateLocation("On Pin")
        }
    }

    override fun onError(e: Exception) {
        Log.i("SearchApiExample", "Reverse geocoding error", e)
    }

}

@Composable
fun AddEventScreen(
    uiState: AddEventUiState,
    profilePictureUrl: String,
    onShowOnMapChanged: (showOnMap: Boolean) -> Unit,
    onDismiss: () -> Unit,
    onUploadEvent: () -> Unit,
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
    PrivacyBottomSheet(uiState = uiState) {
        Scaffold(
            topBar = { TopAppBar(onDismiss = onDismiss) },
        ) {
            Tabs(uiState = uiState)
        }
    }
}

@Composable
fun TopAppBar(
    onDismiss: () -> Unit,
) {
    SmallTopAppBar(
        title = { Text("New event", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.ArrowBack, "")
            }
        },
    )
}

@Composable
fun Tabs(
    uiState: AddEventUiState,
) {
    val tabs = 4
    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = tabs,
            state = pagerState,
            modifier = Modifier.height(600.dp)
        ) { page ->
            Column(modifier = Modifier.fillMaxHeight()) {
                when (page) {
                    0 -> FirstScreen(uiState = uiState)
                    1 -> {
                        Text("1 wd")
                    }
                    2 -> {
                        Text("2 wd")
                    }
                    3 -> {
                        Text("3 wd")
                    }
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
        ShareButton(
            uiState = uiState,
            onDismiss = {},
            onUploadEvent = {},
        )
    }
}

@Composable
fun FirstScreen(
    uiState: AddEventUiState,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        AddPhotoOrVideo(uiState = uiState)
//        NameTextField(uiState = uiState)
        DividerM3()
//        StartDateInput(uiState = uiState)
        DividerM3()
//        TagSection()
        DividerM3()
//        LocationSection()
        DividerM3()
        Description(uiState = uiState)
        DividerM3()
        Privacy(uiState = uiState)
    }
}

@Composable
fun Privacy(uiState: AddEventUiState) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    uiState.privacyState.show()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val privacy = uiState.selectedPrivacy
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
fun PrivacyBottomSheet(
    uiState: AddEventUiState,
    content: @Composable () -> Unit
) {
    val state = uiState.privacyState
    ModalBottomSheetLayout(
        sheetElevation = 0.dp,
        sheetState = state,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxSize(),
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
                val items = listOf(
                    PrivacyItem(
                        icon = Icons.Filled.Lock,
                        title = "Private",
                        subtitle = "Only people who are invited",
                        type = com.salazar.cheers.ui.add.Privacy.PRIVATE
                    ),
                    PrivacyItem(
                        icon = Icons.Filled.Public,
                        title = "Public",
                        subtitle = "Anyone on Cheers",
                        type = com.salazar.cheers.ui.add.Privacy.PUBLIC
                    ),
                    PrivacyItem(
                        icon = Icons.Filled.People,
                        title = "Friends",
                        subtitle = "Your friends on Cheers",
                        type = com.salazar.cheers.ui.add.Privacy.FRIENDS
                    ),
                    PrivacyItem(
                        icon = Icons.Filled.Groups,
                        title = "Group",
                        subtitle = "Members of a group that you're in",
                        type = com.salazar.cheers.ui.add.Privacy.GROUP
                    ),
                )
                items.forEach {
                    Item(it, it == uiState.selectedPrivacy, {})
                }

                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        scope.launch {
                            uiState.privacyState.hide()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Done")
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun Item(
    item: PrivacyItem,
    selected: Boolean,
    onSelectPrivacy: (PrivacyItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onSelectPrivacy(item) }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(item.icon, null)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(item.title)
                Text(item.subtitle)
            }
        }
        Checkbox(
            checked = selected,
            onCheckedChange = { onSelectPrivacy(item) },
        )
    }
}

@Composable
fun StartDateInput(
    uiState: AddEventUiState,
    onStartDateChanged: (String) -> Unit,
    onStartTimeChanged: (String) -> Unit,
    onEndDateChanged: (String) -> Unit,
    onEndTimeChanged: (String) -> Unit,
    onAllDayChanged: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    calendar.time = remember { Date() }
//        viewModel.onStartDateChange("$day/$month/$year")
//        viewModel.onStartTimeChange("$hourOfDay:$minute")
//        viewModel.onEndDateChange("$day/$month/$year")
//        viewModel.onEndTimeChange("$hourOfDay:$minute")

    val startDatePicker = DatePickerDialog(
        context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onStartDateChanged(
                "$year-${
                    (month + 1).toString().padStart(2, '0')
                }-${dayOfMonth.toString().padStart(2, '0')}"
            )

        }, year, month, day
    )
    val endDatePicker = DatePickerDialog(
        context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onEndDateChanged(
                "$year-${
                    (month + 1).toString().padStart(2, '0')
                }-${dayOfMonth.toString().padStart(2, '0')}"
            )
        }, year, month, day
    )
    val startTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            onStartTimeChanged(
                "${hourOfDay.toString().padStart(2, '0')}:${
                    minute.toString().padStart(2, '0')
                }"
            )
        }, hourOfDay, minute, true
    )

    val endTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            onEndTimeChanged(
                "${hourOfDay.toString().padStart(2, '0')}:${
                    minute.toString().padStart(2, '0')
                }"
            )
        }, hourOfDay, minute, true
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Schedule, null, modifier = Modifier.offset(y = 12.dp))
            Column {
                SwitchPreference(
                    value = uiState.allDay,
                    text = "All-day"
                ) { onAllDayChanged(it) }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { startDatePicker.show() },
                        text = uiState.startDate,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!uiState.allDay)
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { startTimePicker.show() },
                            text = uiState.startTime,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { endDatePicker.show() },
                        text = uiState.endDate,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!uiState.allDay)
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { endTimePicker.show() },
                            text = uiState.endTime,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                }
            }
        }
    }
}

@Composable
fun DescriptionInput(
    uiState: AddEventUiState,
    onDescriptionChanged: (String) -> Unit,
) {
    val description = uiState.description
    val focusManager = LocalFocusManager.current
    TextField(
        value = description,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        onValueChange = { onDescriptionChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Description") },
        enabled = !uiState.isLoading,
    )
}

@Composable
fun NameTextField(
    uiState: AddEventUiState,
    onEventNameChanged: (String) -> Unit,
) {
    val eventName = uiState.name
    val focusManager = LocalFocusManager.current
    TextField(
        value = eventName,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(start = 36.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        onValueChange = { onEventNameChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Add title", style = MaterialTheme.typography.titleLarge) },
        enabled = !uiState.isLoading,
        trailingIcon = {
            val photoUri = uiState.imageUri
            if (photoUri != null) {
                Image(
                    modifier = Modifier
                        .clickable(onClick = { openPhotoVideoChooser() })
                        .padding(horizontal = 16.dp)
                        .size(50.dp),
                    painter = rememberImagePainter(data = uiState.imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}

@Composable
fun ShareButton(
    uiState: AddEventUiState,
    onUploadEvent: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        DividerM3()
        Button(
            onClick = {
                onUploadEvent()
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = uiState.name.isNotBlank()
        ) {
            Text("Share")
        }
    }
}

@Composable
fun SwitchPreference(
    value: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
        SwitchM3(
            modifier = Modifier.background(Color.Red),
            checked = value,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun AddPhotoOrVideo(uiState: AddEventUiState) {
    if (uiState.imageUri != null)
        return

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        FilledTonalButton(onClick = { openPhotoVideoChooser() }) {
            Icon(Icons.Outlined.PhotoCamera, "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add photo or video")
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
fun LocationResultsSection(results: List<SearchResult>) {
    if (results.isNotEmpty())
        LocationResult(results = results, {})
}

@Composable
fun LocationResultsSection(
    results: List<SearchResult>,
    onSelectLocation: (SearchResult) -> Unit,
) {
    if (results.isNotEmpty())
        LocationResult(
            results = results,
            onSelectLocation = onSelectLocation
        )
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
fun Description(uiState: AddEventUiState) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Description, null)
//        DescriptionInput(uiState = uiState)
    }
}

@Composable
fun TagSection(
    navigateToTagUsers: () -> Unit,
    tagUsers: List<User>,
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Icon(Icons.Outlined.People, null)
        Column {
            Row(
                modifier = Modifier
                    .clickable { navigateToTagUsers() }
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Add people",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
                if (tagUsers.size == 1)
                    Text(
                        text = tagUsers[0].username,
                        style = MaterialTheme.typography.labelLarge
                    )
                if (tagUsers.size > 1)
                    Text(
                        text = "${tagUsers.size} people",
                        style = MaterialTheme.typography.labelLarge
                    )
            }
        }
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