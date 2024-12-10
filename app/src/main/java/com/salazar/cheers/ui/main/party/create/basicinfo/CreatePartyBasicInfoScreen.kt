package com.salazar.cheers.ui.main.party.create.basicinfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.components.datepicker.DatePickerField
import com.salazar.cheers.core.ui.components.datepicker.TimePickerField
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.util.relativeTimeFormatterMilli
import com.salazar.cheers.feature.create_post.PrivacyBottomSheet
import com.salazar.cheers.feature.create_post.SwitchPreference
import com.salazar.cheers.ui.main.party.create.CreatePartyUiState
import com.salazar.cheers.ui.main.party.create.recap.Privacy
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun CreatePartyBasicInfoScreen(
    uiState: CreatePartyUiState,
    onNameChange: (String) -> Unit,
    onStartDateChange: (Long) -> Unit,
    onEndDateChange: (Long) -> Unit,
    onHasEndDateToggle: () -> Unit,
    onShowGuestListToggle: () -> Unit,
    navigateBack: () -> Unit,
    onPrivacyChange: (Privacy) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            CreatePartyBasicInfoBottomBar(
                enabled = uiState.name.isNotBlank(),
                onClick = navigateBack,
            )
        },
        topBar = {
            Toolbar(
                onBackPressed = navigateBack,
                title = "Basic info",
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            NameTextField(
                name = uiState.name,
                onEventNameChanged = onNameChange,
            )
            StartDateInput(
                uiState = uiState,
                onStartDateChange = onStartDateChange,
                onHasEndDateToggle = onHasEndDateToggle,
                onEndDateChange = onEndDateChange,
            )
            HorizontalDivider()
            Privacy(
                privacy = uiState.privacy,
                onShowClick = {
                    scope.launch {
                        sheetState.show()
                    }.invokeOnCompletion {
                        showBottomSheet = true
                    }
                },
            )
        }
    }
    if (showBottomSheet) {
        PrivacyBottomSheet(
            privacy = uiState.privacy,
            privacyState = sheetState,
            onSelectPrivacy = onPrivacyChange,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showBottomSheet = false
                }
            }
        )
    }
}


@Composable
fun StartDateInput(
    uiState: CreatePartyUiState,
    onStartDateChange: (Long) -> Unit,
    onEndDateChange: (Long) -> Unit,
    onHasEndDateToggle: () -> Unit,
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.time = remember { Date() }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Start date and time",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DatePickerField(
                selectedDateMillis = uiState.startDateTimeMillis,
                onDateSelected = {
                    if (it == null) return@DatePickerField
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = uiState.startDateTimeMillis
                    calendar.timeInMillis = it
                    calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY))
                    calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE))
                    onStartDateChange(calendar.timeInMillis)
                }
            )
            TimePickerField(
                selectedTime = uiState.startDateTimeMillis,
                onTimeSelected = { hour, minute ->
                    calendar.timeInMillis = uiState.startDateTimeMillis
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    onStartDateChange(calendar.timeInMillis)
                }
            )
        }
        AnimatedVisibility(
            visible = uiState.hasEndDate,
        ) {
            Column() {
                Text(
                    text = "End date and time",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DatePickerField(
                        selectedDateMillis = uiState.endDateTimeMillis,
                        onDateSelected = {
                            if (it == null) return@DatePickerField
                            val calendar2 = Calendar.getInstance()
                            calendar2.timeInMillis = uiState.endDateTimeMillis
                            calendar.timeInMillis = it
                            calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE))
                            onEndDateChange(calendar.timeInMillis)
                        }
                    )
                    TimePickerField(
                        selectedTime = uiState.endDateTimeMillis,
                        onTimeSelected = { hour, minute ->
                            calendar.timeInMillis = uiState.endDateTimeMillis
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            onEndDateChange(calendar.timeInMillis)
                        }
                    )
                }
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = relativeTimeFormatterMilli(
                        initialTimeMillis = uiState.startDateTimeMillis,
                        value = uiState.endDateTimeMillis,
                    ),
                )
            }
        }
    }
    SwitchPreference(
        checked = uiState.hasEndDate,
        text = "With end date",
        onCheckedChange = { onHasEndDateToggle() },
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
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        placeholder = {
            Text(
                text = "Event name*",
                style = MaterialTheme.typography.titleMedium,
            )
        },
    )
}


@Preview
@Composable
private fun CreatePartyBasicInfoScreenPreview() {
    CheersPreview {
        CreatePartyBasicInfoScreen(
            uiState = CreatePartyUiState(isLoading = false),
            onNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onHasEndDateToggle = { /*TODO*/ },
            onShowGuestListToggle = {},
            navigateBack = {},
            onPrivacyChange = {},

            )
    }
}