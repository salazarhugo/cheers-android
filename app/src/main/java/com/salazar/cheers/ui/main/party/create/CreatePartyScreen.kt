package com.salazar.cheers.ui.main.party.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.mapbox.search.result.SearchSuggestion
import com.salazar.cheers.feature.chat.ui.components.messageFormatter
import com.salazar.cheers.core.data.internal.Privacy
import com.salazar.cheers.core.util.startDateFormatter
import com.salazar.cheers.core.util.timeFormatter
import com.salazar.cheers.ui.compose.DividerM3
import com.salazar.cheers.ui.compose.event.EventDetails
import com.salazar.cheers.ui.main.add.LocationSection
import com.salazar.cheers.ui.main.add.PrivacyBottomSheet
import com.salazar.cheers.ui.main.add.SwitchPreference
import com.salazar.cheers.ui.main.search.SearchLocation
import com.salazar.cheers.core.ui.theme.Roboto
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun CreatePartyScreen(
    uiState: CreatePartyUiState,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
    onPrivacyChange: (Privacy) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
    onShowGuestListToggle: () -> Unit,
) {
    PrivacyBottomSheet(
        privacy = uiState.privacy,
        privacyState = uiState.privacyState,
        onSelectPrivacy = onPrivacyChange,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    onDismiss = { onCreatePartyUIAction(CreatePartyUIAction.OnDismiss) },
                    title = "New Party"
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                val pagerState = rememberPagerState()
                val scope = rememberCoroutineScope()
                Tabs(
                    uiState = uiState,
                    modifier = Modifier.weight(1f),
                    pagerState = pagerState,
                    onCreatePartyUIAction = onCreatePartyUIAction,
                    onNameChange = onNameChange,
                    onDescriptionChange = onDescriptionChange,
                    onStartTimeSecondsChange = onStartTimeSecondsChange,
                    onEndTimeSecondsChange = onEndTimeSecondsChange,
                    onQueryChange = onQueryChange,
                    onLocationClick = onLocationClick,
                    onShowGuestListToggle = onShowGuestListToggle,
                )
                ShareButton(
                    page = pagerState.currentPage,
                    uiState = uiState,
                    onClick = {
                        if (pagerState.currentPage == 3)
                            onCreatePartyUIAction(CreatePartyUIAction.OnUploadParty)
                        else
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun TopAppBar(
    title: String,
    onDismiss: () -> Unit,
) {
    TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.ArrowBack, "")
            }
        })
}

@Composable
fun Tabs(
    uiState: CreatePartyUiState,
    pagerState: PagerState,
    modifier: Modifier,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
    onShowGuestListToggle: () -> Unit,
) {
    val tabs = 4
    val scope = rememberCoroutineScope()

    HorizontalPager(
        modifier = modifier,
        count = tabs,
        state = pagerState,
        userScrollEnabled = false,
    ) { page ->
        Column(modifier = Modifier.fillMaxHeight()) {
            when (page) {
                0 -> EventDetailsPage(
                    uiState = uiState,
                    onEventNameChange = onNameChange,
                    onStartDateChanged = onStartTimeSecondsChange,
                    onEndTimeSecondsChange = onEndTimeSecondsChange,
                    onHasEndDateToggle = { onCreatePartyUIAction(CreatePartyUIAction.OnHasEndDateToggle) },
                    onShowGuestListToggle = onShowGuestListToggle,
                )
                1 -> DescriptionPage(
                    description = uiState.description,
                    onDescriptionChange = onDescriptionChange,
                )
                2 -> LocationPage(
                    locationName = uiState.locationName,
                    query = uiState.locationQuery,
                    results = uiState.locationResults,
                    onQueryChange = onQueryChange,
                    onLocationClick = onLocationClick,
                )
                3 -> FirstScreen(
                    uiState = uiState,
                    onCreatePartyUIAction = {
                        when (it) {
                            CreatePartyUIAction.OnPartyDetailsClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            CreatePartyUIAction.OnDescriptionClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            CreatePartyUIAction.OnLocationClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            else -> {}
                        }
                        onCreatePartyUIAction(it)
                    },
                )
            }
        }
    }
}

@Composable
fun FirstScreen(
    uiState: CreatePartyUiState,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        AddPhoto(
            photo = uiState.photo,
            onAddPhotoClick = { onCreatePartyUIAction(CreatePartyUIAction.OnAddPhoto) },
        )
        EventDetails(
            name = uiState.name,
            privacy = uiState.privacy,
            startTimeSeconds = uiState.startTimeSeconds,
            onEventDetailsClick = { onCreatePartyUIAction(CreatePartyUIAction.OnPartyDetailsClick) }
        )
        DividerM3()
        Description(
            description = uiState.description,
            onDescriptionClick = { onCreatePartyUIAction(CreatePartyUIAction.OnDescriptionClick) }
        )
        DividerM3()
        CategorySection(
            category = "",
            onClick = {},
        )
        DividerM3()
        LocationSection("", navigateToChooseOnMap = {})
//        NameTextField(uiState = uiState)
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
fun Item(
    item: Privacy,
    selected: Boolean,
    onSelectPrivacy: (Privacy) -> Unit,
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
    uiState: CreatePartyUiState,
    onStartDateChanged: (Long) -> Unit,
    onEndDateChanged: (Long) -> Unit,
    onHasEndDateToggle: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    calendar.time = remember { Date() }

    val startDatePicker = DatePickerDialog(
        context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000L)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onStartDateChanged(calendar.time.time / 1000)
        }, year, month, day
    )
    val endDatePicker = DatePickerDialog(
        context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000L)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onEndDateChanged(calendar.time.time / 1000)
        }, year, month, day
    )
    val startTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000L)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            onStartDateChanged(calendar.time.time / 1000)
        }, hourOfDay, minute, true
    )

    val endTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000L)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            onEndDateChanged(calendar.time.time / 1000)
        }, hourOfDay, minute, true
    )

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Starts",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .clickable { startDatePicker.show() },
                text = com.salazar.cheers.core.util.startDateFormatter(timestamp = uiState.startTimeSeconds.toLong()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                modifier = Modifier
                    .clickable { startTimePicker.show() },
                text = com.salazar.cheers.core.util.timeFormatter(timestamp = uiState.startTimeSeconds.toLong()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (uiState.hasEndDate) {
            Text(
                text = "Ends",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickable { endDatePicker.show() },
                    text = com.salazar.cheers.core.util.startDateFormatter(timestamp = uiState.endTimeSeconds.toLong()),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    modifier = Modifier
                        .clickable { endTimePicker.show() },
                    text = com.salazar.cheers.core.util.timeFormatter(timestamp = uiState.endTimeSeconds.toLong()),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        TextButton(onClick = onHasEndDateToggle) {
            val text = if (uiState.hasEndDate) "Remove End Date" else "Add End Date"
            Text(text = text)
        }
    }
}

@Composable
fun DescriptionInput(
    description: String,
    onDescriptionChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = description,
        modifier = Modifier
            .fillMaxWidth(),
        onValueChange = { onDescriptionChanged(it) },
        shape = MaterialTheme.shapes.medium,
        singleLine = false,
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Text,
//            imeAction = ImeAction.Next
//        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Description") },
    )
}

@Composable
fun NameTextField(
    name: String,
    onEventNameChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = name,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
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
        placeholder = { Text("Event name", style = MaterialTheme.typography.titleMedium) },
    )
}

@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    page: Int,
    uiState: CreatePartyUiState,
    onClick: () -> Unit,
) {
    val text = if (page == 3) "Create Event" else "Next"
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        DividerM3()
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = uiState.name.isNotBlank()
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun AddPhoto(
    photo: Uri?,
    onAddPhotoClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .background(Color.Gray),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            AsyncImage(
                model = photo.toString(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clickable { onAddPhotoClick() },
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        } else {
            FilledTonalButton(onClick = onAddPhotoClick) {
                Icon(Icons.Outlined.PhotoCamera, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add photo")
            }
        }
    }
}

@Composable
fun CategorySection(
    category: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Category, null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = category.ifBlank { "Add a category" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                )

            }
        }
        Icon(Icons.Outlined.ChevronRight, null)
    }
}

@Composable
fun Description(
    description: String,
    onDescriptionClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable { onDescriptionClick() }
            .padding(16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Icon(Icons.Outlined.Edit, null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                )
                val styledDescription = messageFormatter(
                    text = description,
                    primary = false,
                )
                Text(
                    text = styledDescription.ifBlank { buildAnnotatedString { append("Add a description") } },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                )
            }
        }
        Icon(Icons.Outlined.ChevronRight, null)
    }
}

@Composable
fun LocationPage(
    locationName: String,
    query: String,
    results: List<SearchSuggestion>,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    var search by remember { mutableStateOf(false) }

    if (search)
        SearchLocation(
            searchInput = query,
            results = results,
            onSearchInputChanged = onQueryChange,
            onLocationClick = {
                onLocationClick(it)
                search = false
            },
        )
    else
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Add a physical location for people to join your event.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            OutlinedTextField(
                value = locationName,
                onValueChange = {},
                placeholder = {
                    Text("Add a location")
                },
                modifier = Modifier
                    .clickable {
                        search = true
                    },
                readOnly = true,
                enabled = false
            )
        }
}

@Composable
fun DescriptionPage(
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = "Provide more information about your event so that guests know what to expect.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        DescriptionInput(
            description = description,
            onDescriptionChanged = onDescriptionChange,
        )
    }
}

@Composable
fun EventDetailsPage(
    uiState: CreatePartyUiState,
    onEventNameChange: (String) -> Unit,
    onStartDateChanged: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onHasEndDateToggle: () -> Unit,
    onShowGuestListToggle: () -> Unit,
) {
    NameTextField(
        name = uiState.name,
        onEventNameChanged = onEventNameChange,
    )
    StartDateInput(
        uiState = uiState,
        onStartDateChanged = onStartDateChanged,
        onEndDateChanged = onEndTimeSecondsChange,
        onHasEndDateToggle = onHasEndDateToggle,
    )
    DividerM3()
//    Privacy(
//        privacyState = uiState.privacyState,
//        privacy = uiState.privacy,
//    )
    DividerM3()
    SwitchPreference(
        checked = uiState.showGuestList,
        text = "Show Guest List",
        onCheckedChange = { onShowGuestListToggle() },
    )
}