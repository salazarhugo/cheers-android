package com.salazar.cheers.ui.main.party.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.compose.DividerM3
import com.salazar.cheers.compose.event.*
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.numberFormatter
import com.salazar.cheers.ui.theme.GreySheet
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(
    uiState: EventDetailUiState,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onCopyLink: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onInterestedToggle: (Party) -> Unit,
    onGoingToggle: (Party) -> Unit,
    onGoingCountClick: () -> Unit,
    onInterestedCountClick: () -> Unit,
    onTicketingClick: (String) -> Unit,
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
                        party = uiState.party,
                        onMapClick = onMapClick,
                        onUserClicked = onUserClicked,
                        onManageClick = {
                            scope.launch {
                                state.show()
                            }
                        },
                        onInterestedToggle = onInterestedToggle,
                        onGoingToggle = onGoingToggle,
                        onGoingCountClick = onGoingCountClick,
                        onInterestedCountClick = onInterestedCountClick,
                        onTicketingClick = onTicketingClick,
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
    party: Party,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onManageClick: () -> Unit,
    onInterestedToggle: (Party) -> Unit,
    onGoingToggle: (Party) -> Unit,
    onGoingCountClick: () -> Unit,
    onInterestedCountClick: () -> Unit,
    onTicketingClick: (String) -> Unit,
) {
    val uid = remember { FirebaseAuth.getInstance().currentUser?.uid!! }
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(state = state) {

        item {
            EventHeader(
                party = party,
                onAboutClick = {
                    scope.launch {
                        state.animateScrollToItem(1)
                    }
                },
                onManageClick = onManageClick,
                onInterested = onInterestedToggle,
                onGoing = onGoingToggle,
                onTicketingClick = onTicketingClick,
            )
        }

        if (party.hostId == uid || party.showGuestList)
            item {
                EventResponses(
                    party = party,
                    onGoingCountClick = onGoingCountClick,
                    onInterestedCountClick = onInterestedCountClick,
                )
                DividerM3()
            }

        item {
            EventDescription(
                description = party.description,
                modifier = Modifier.padding(16.dp),
                onUserClicked = onUserClicked,
            )
            DividerM3()
        }

        item {
            EventVenue(
                address = "Avenue Darcel, 75019",
                latitude = party.latitude,
                longitude = party.longitude,
                modifier = Modifier.padding(16.dp),
                onMapClick = onMapClick,
            )
        }
    }
}

@Composable
fun EventResponses(
    party: Party,
    onInterestedCountClick: () -> Unit,
    onGoingCountClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            "Guest list",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .clickable { onInterestedCountClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Interested",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = numberFormatter(value = party.interestedCount),
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .clickable { onGoingCountClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Going",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = numberFormatter(value = party.goingCount),
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
        }
    }
}

@Composable
fun EventHeader(
    party: Party,
    onAboutClick: () -> Unit,
    onManageClick: () -> Unit,
    onGoing: (Party) -> Unit,
    onInterested: (Party) -> Unit,
    onTicketingClick: (String) -> Unit,
) {
    AsyncImage(
        model = party.bannerUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center
    )
    Column {
        EventDetails(
            name = party.name,
            privacy = party.privacy,
            startTimeSeconds = party.startDate,
            onEventDetailsClick = {},
        )
        EventHeaderButtons(
            party.hostId,
            onManageClick = onManageClick,
            onGoingClick = { onGoing(party) },
            onInterestedClick = { onInterested(party) },
            onInviteClick = {},
            interested = party.interested,
            going = party.going,
        )
        EventInfo(
            locationName = party.locationName,
            address = party.address,
            hostName = party.hostName,
            price = party.price,
            privacy = party.privacy,
            startTimeSeconds = party.startDate,
            interestedCount = party.interestedCount,
            goingCount = party.goingCount,
            onTicketingClick = { onTicketingClick(party.id) },
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