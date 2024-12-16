package com.salazar.cheers.feature.parties.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.core.util.numberFormatter
import com.salazar.cheers.feature.parties.ui.PartyDescription
import com.salazar.cheers.feature.parties.ui.PartyLineup
import com.salazar.cheers.feature.parties.ui.PartyMood
import com.salazar.cheers.feature.parties.ui.PartyVenue
import kotlinx.coroutines.launch

@Composable
fun PartyDetailScreen(
    uiState: PartyDetailUiState,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onCopyLink: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onWatchStatusChange: (WatchStatus) -> Unit,
    onGoingCountClick: () -> Unit,
    onInterestedCountClick: () -> Unit,
    onTicketingClick: (String) -> Unit,
    onAnswersClick: () -> Unit,
) {
    val state = rememberModalBottomSheetState(true)
    val scope = rememberCoroutineScope()

//    ModalBottomSheetLayout(
//        sheetState = state,
//        sheetContent = {
//            PartyManageSheet(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .systemBarsPadding(),
//                onCopyLink = onCopyLink,
//                onEditClick = onEditClick,
//                onDeleteClick = onDeleteClick,
//            )
//        },
//        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
//        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
//    )
    Scaffold {
        it
        Box(
//            modifier = Modifier.padding(it)
        ) {
            when (uiState) {
                is PartyDetailUiState.HasParty -> PartyDetail(
                    party = uiState.party,
                    onMapClick = onMapClick,
                    onUserClicked = onUserClicked,
                    onManageClick = {
                        scope.launch {
                            state.show()
                        }
                    },
                    onWatchStatusChange = onWatchStatusChange,
                    onGoingCountClick = onGoingCountClick,
                    onInterestedCountClick = onInterestedCountClick,
                    onTicketingClick = onTicketingClick,
                    onAnswersClick = onAnswersClick,
                )

                is PartyDetailUiState.NoPartys -> {
                    Text("No event")
                }
            }
        }
    }
}

@Composable
fun PartyDetail(
    party: Party,
    onMapClick: () -> Unit,
    onUserClicked: (String) -> Unit,
    onManageClick: () -> Unit,
    onWatchStatusChange: (WatchStatus) -> Unit,
    onGoingCountClick: () -> Unit,
    onInterestedCountClick: () -> Unit,
    onTicketingClick: (String) -> Unit,
    onAnswersClick: () -> Unit,
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(state = state) {
        header(
            party = party,
            onManageClick = onManageClick,
            onWatchStatusChange = onWatchStatusChange,
            onTicketingClick = onTicketingClick,
            onUserClick = onUserClicked,
            onAnswersClick = onAnswersClick,
            onAboutClick = {
                scope.launch {
                    state.animateScrollToItem(1)
                }
            },
        )
        description(
            description = party.description,
            onUserClicked = onUserClicked,
        )
        guestList(party = party)
        lineup(lineup = party.lineup)
        mood(musicGenres = party.musicGenres)
        venue(
            address = party.address,
            latitude = party.latitude,
            longitude = party.longitude,
            onMapClick = onMapClick,
        )
    }
}

fun LazyListScope.header(
    party: Party,
    onAboutClick: () -> Unit,
    onManageClick: () -> Unit,
    onWatchStatusChange: (WatchStatus) -> Unit,
    onTicketingClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onAnswersClick: () -> Unit,
) {
    item {
        PartyHeader(
            party = party,
            onAboutClick = onAboutClick,
            onManageClick = onManageClick,
            onWatchStatusChange = onWatchStatusChange,
            onTicketingClick = onTicketingClick,
            onUserClick = onUserClick,
            onAnswersClick = onAnswersClick,
        )
    }
}

fun LazyListScope.description(
    description: String,
    onUserClicked: (String) -> Unit,
) {
    if (description.isBlank()) return

    item {
        PartyDescription(
            description = description,
            modifier = Modifier.padding(16.dp),
            onUserClicked = onUserClicked,
        )
    }
}

fun LazyListScope.venue(
    address: String,
    latitude: Double,
    longitude: Double,
    onMapClick: () -> Unit,
) {
    item {
        PartyVenue(
            modifier = Modifier
                .animateItem()
                .padding(16.dp),
            address = address,
            latitude = latitude,
            longitude = longitude,
            onMapClick = onMapClick,
        )
    }
}

fun LazyListScope.lineup(lineup: List<String>) {
    if (lineup.isEmpty()) return

    item {
        PartyLineup(
            modifier = Modifier.padding(top = 16.dp),
            lineup = lineup,
        )
    }
}

fun LazyListScope.mood(musicGenres: List<String>) {
    if (musicGenres.isEmpty()) return

    item {
        PartyMood(
            modifier = Modifier.padding(top = 16.dp),
            musicGenres = musicGenres,
        )
    }
}

fun LazyListScope.guestList(party: Party) {
    if (!party.isHost || !party.showGuestList)
        return

    item {
        PartyResponses(
            goingCount = party.goingCount,
            interestedCount = party.interestedCount,
            onGoingCountClick = {},
            onInterestedCountClick = {},
        )
    }
}

@Composable
fun PartyResponses(
    interestedCount: Int,
    goingCount: Int,
    onInterestedCountClick: () -> Unit,
    onGoingCountClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Guest list",
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
                    text = numberFormatter(value = interestedCount),
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
                    text = numberFormatter(value = goingCount),
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
        }
    }
}