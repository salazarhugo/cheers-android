package com.salazar.cheers.ui.main.event.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.event.*
import com.salazar.cheers.internal.*
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(
    uiState: EventDetailUiState,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onCopyLink: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onGoingToggle: (Event) -> Unit,
) {
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            EventManageSheet(
                onCopyLink = onCopyLink,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
            )
        },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
    ) {
        Scaffold {
            Box(
                modifier = Modifier.padding(it)
            ) {
                when (uiState) {
                    is EventDetailUiState.HasEvent -> Event(
                        event = uiState.event,
                        onMapClick = onMapClick,
                        onUserClicked = onUserClicked,
                        onManageClick = {
                            scope.launch {
                                state.show()
                            }
                        },
                        onInterestedToggle = onInterestedToggle,
                        onGoingToggle = onGoingToggle,
                    )
                    is EventDetailUiState.NoEvents -> {
                        Text("No event")
                    }
                }
            }
        }
    }
}

@Composable
fun Event(
    event: Event,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onManageClick: () -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onGoingToggle: (Event) -> Unit,
) {
    val state =  rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(state = state) {

        item {
            EventHeader(
                event = event,
                onAboutClick = {
                    scope.launch {
                        state.animateScrollToItem(1)
                    }
                },
                onManageClick = onManageClick,
                onInterested = {},
                onGoing = {},
            )
        }

        item {
            EventDescription(
                description = event.description,
                modifier = Modifier.padding(16.dp),
                onUserClicked = onUserClicked,
            )
            DividerM3()
        }

        item {
            EventVenue(
                address = "Avenue Darcel, 75019",
                latitude = event.latitude,
                longitude = event.longitude,
                modifier = Modifier.padding(16.dp),
                onMapClick = onMapClick,
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                EventInterestButton(
                    interested = event.interested,
                    modifier = Modifier.weight(1f),
                    onInterestedToggle = { onInterestedToggle(event)},
                )
                EventGoingButton(
                    going = event.going,
                    modifier = Modifier.weight(1f),
                    onGoingToggle = { onGoingToggle(event)},
                )
            }
        }
    }
}

@Composable
fun EventHeader(
    event: Event,
    onAboutClick: () -> Unit,
    onManageClick: () -> Unit,
    onGoing: () -> Unit,
    onInterested: () -> Unit,
) {
    AsyncImage(
        model = event.imageUrl,
        contentDescription =null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center
    )
    Column {
        EventDetails(
            name = event.name,
            privacy = event.privacy,
            startTimeSeconds = event.startDate,
            onEventDetailsClick = {},
        )
        EventHeaderButtons(
            event.hostId,
            onManageClick = onManageClick,
            onGoingClick = onGoing,
            onInterestedClick = onInterested,
            onInviteClick = {},
        )
        EventInfo(
            locationName = event.locationName,
            address = event.address,
            hostName = event.hostName,
            price = event.price,
            privacy = event.privacy,
            startTimeSeconds = event.startDate,
            interestedCount = event.interestedCount,
            goingCount = event.goingCount,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            FilledTonalButton(
                onClick = onAboutClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("About")
            }
            FilledTonalButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Discussion")
            }
        }
        DividerM3()
    }
}