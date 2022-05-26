package com.salazar.cheers.ui.main.event.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.mapbox.search.result.SearchSuggestion
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.components.event.EventDetails
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.startDateFormatter
import com.salazar.cheers.internal.timeFormatter
import com.salazar.cheers.ui.main.add.LocationSection
import com.salazar.cheers.ui.main.add.PrivacyBottomSheet
import com.salazar.cheers.ui.main.chat.messageFormatter
import com.salazar.cheers.ui.main.search.SearchLocation
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun AddEventScreen(
    uiState: AddEventUiState,
    onAddEventUIAction: (AddEventUIAction) -> Unit,
    onPrivacyChange: (Privacy) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    PrivacyBottomSheet(
        privacy = uiState.privacy,
        privacyState = uiState.privacyState,
        onSelectPrivacy = onPrivacyChange,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    onDismiss = { onAddEventUIAction(AddEventUIAction.OnDismiss) },
                    title = "New Event"
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                Tabs(
                    uiState = uiState,
                    onAddEventUIAction = onAddEventUIAction,
                    onNameChange = onNameChange,
                    onDescriptionChange = onDescriptionChange,
                    onStartTimeSecondsChange = onStartTimeSecondsChange,
                    onEndTimeSecondsChange = onEndTimeSecondsChange,
                    onQueryChange = onQueryChange,
                    onLocationClick = onLocationClick,
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
    SmallTopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
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
    onAddEventUIAction: (AddEventUIAction) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    val tabs = 4
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    HorizontalPager(
        count = tabs,
        state = pagerState,
        modifier = Modifier.height(600.dp),
        userScrollEnabled = false,
    ) { page ->
        Column(modifier = Modifier.fillMaxHeight()) {
            when (page) {
                0 -> EventDetailsPage(
                    uiState = uiState,
                    onEventNameChange = onNameChange,
                    onStartDateChanged = onStartTimeSecondsChange,
                    onEndTimeSecondsChange = onEndTimeSecondsChange,
                    onHasEndDateToggle = { onAddEventUIAction(AddEventUIAction.OnHasEndDateToggle) }
                )
                1 -> DescriptionPage(
                    uiState = uiState,
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
                    onAddEventUIAction = {
                        when (it) {
                            AddEventUIAction.OnEventDetailsClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            AddEventUIAction.OnDescriptionClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            AddEventUIAction.OnLocationClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                        }
                        onAddEventUIAction(it)
                    },
                )
            }
        }
    }
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.padding(16.dp),
    )
    ShareButton(
        page = pagerState.currentPage,
        uiState = uiState,
        onClick = {
            if (pagerState.currentPage == 3)
                onAddEventUIAction(AddEventUIAction.OnUploadEvent)
            else
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
        }
    )
}

@Composable
fun FirstScreen(
    uiState: AddEventUiState,
    onAddEventUIAction: (AddEventUIAction) -> Unit,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        AddPhoto(
            photo = uiState.photo,
            onAddPhotoClick = { onAddEventUIAction(AddEventUIAction.OnAddPhoto) },
        )
        EventDetails(
            name = uiState.name,
            privacy = uiState.privacy,
            startTimeSeconds = uiState.startTimeSeconds,
            onEventDetailsClick = { onAddEventUIAction(AddEventUIAction.OnEventDetailsClick) }
        )
        DividerM3()
        Description(
            description = uiState.description,
            onDescriptionClick = { onAddEventUIAction(AddEventUIAction.OnDescriptionClick) }
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
    uiState: AddEventUiState,
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
            calendar.time = Date(uiState.startTimeSeconds * 1000)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onStartDateChanged(calendar.time.time / 1000)
        }, year, month, day
    )
    val endDatePicker = DatePickerDialog(
        context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onEndDateChanged(calendar.time.time / 1000)
        }, year, month, day
    )
    val startTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            onStartDateChanged(calendar.time.time / 1000)
        }, hourOfDay, minute, true
    )

    val endTimePicker = TimePickerDialog(
        context, { _: TimePicker, hourOfDay: Int, minute: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(uiState.startTimeSeconds * 1000)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            onEndDateChanged(calendar.time.time / 1000)
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
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { startDatePicker.show() },
                        text = startDateFormatter(timestamp = uiState.startTimeSeconds),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { startTimePicker.show() },
                        text = timeFormatter(timestamp = uiState.startTimeSeconds),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (uiState.hasEndDate)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { endDatePicker.show() },
                            text = startDateFormatter(timestamp = uiState.endTimeSeconds),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { endTimePicker.show() },
                            text = timeFormatter(timestamp = uiState.endTimeSeconds),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                TextButton(onClick = onHasEndDateToggle) {
                    val text = if (uiState.hasEndDate) "Remove End Date" else "Add End Date"
                    Text(text = text)
                }
            }
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
        shape = RoundedCornerShape(8.dp),
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
        shape = RoundedCornerShape(8.dp),
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
    page: Int,
    uiState: AddEventUiState,
    onClick: () -> Unit,
) {
    val text = if (page == 3) "Create Event" else "Next"
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
            enabled = uiState.name.isNotBlank()
        ) {
            Text(text = text)
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
            Column() {
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
            Column() {
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
    uiState: AddEventUiState,
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
            description = uiState.description,
            onDescriptionChanged = onDescriptionChange,
        )
    }
}

@Composable
fun EventDetailsPage(
    uiState: AddEventUiState,
    onEventNameChange: (String) -> Unit,
    onStartDateChanged: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onHasEndDateToggle: () -> Unit,
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
    Privacy(
        privacyState = uiState.privacyState,
        privacy = uiState.privacy,
    )
}