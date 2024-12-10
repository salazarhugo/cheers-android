package com.salazar.cheers.feature.parties.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
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
import com.salazar.cheers.core.ui.components.party.PartyBannerComponent
import com.salazar.cheers.core.util.numberFormatter
import com.salazar.cheers.feature.parties.ui.PartyDescription
import com.salazar.cheers.feature.parties.ui.PartyDetails
import com.salazar.cheers.feature.parties.ui.PartyHeaderButtons
import com.salazar.cheers.feature.parties.ui.PartyInfo
import com.salazar.cheers.feature.parties.ui.PartyVenue
import com.salazar.cheers.shared.util.LocalActivity
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

        item {
            PartyHeader(
                party = party,
                onAboutClick = {
                    scope.launch {
                        state.animateScrollToItem(1)
                    }
                },
                onManageClick = onManageClick,
                onWatchStatusChange = onWatchStatusChange,
                onTicketingClick = onTicketingClick,
                onUserClick = onUserClicked,
                onAnswersClick = onAnswersClick,
            )
        }

        guestList(party)

        if (party.description.isNotBlank()) {
            item {
                PartyDescription(
                    description = party.description,
                    modifier = Modifier.padding(16.dp),
                    onUserClicked = onUserClicked,
                )
                HorizontalDivider()
            }
        }

        item {
            PartyVenue(
                address = party.address,
                latitude = party.latitude,
                longitude = party.longitude,
                modifier = Modifier.padding(16.dp),
                onMapClick = onMapClick,
            )
        }
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
        HorizontalDivider()
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

@Composable
fun PartyHeader(
    party: Party,
    onAboutClick: () -> Unit,
    onManageClick: () -> Unit,
    onWatchStatusChange: (WatchStatus) -> Unit,
    onTicketingClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onAnswersClick: () -> Unit,
) {
    val activity = LocalActivity.current
    val gmmIntentUri = Uri.parse("geo:${party.longitude},${party.latitude}?q=${party.address}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

    PartyBannerComponent(
        modifier = Modifier.statusBarsPadding(),
        bannerUrl = party.bannerUrl,
    )
    Column {
        PartyDetails(
            name = party.name,
            privacy = party.privacy,
            startTimeSeconds = party.startDate,
            onPartyDetailsClick = {},
        )
        PartyHeaderButtons(
            isHost = party.isHost,
            onManageClick = onManageClick,
            onWatchStatusChange = onWatchStatusChange,
            onInviteClick = {},
            watchStatus = party.watchStatus,
        )
        PartyInfo(
            city = party.city,
            startDate = party.startDate,
            privacy = party.privacy,
            goingCount = party.goingCount,
            interestedCount = party.interestedCount,
            price = party.price,
            hostName = party.hostName,
            address = party.address,
            hostId = party.hostId,
            onTicketingClick = { onTicketingClick(party.id) },
            onUserClick = onUserClick,
            onAddressClick = {
                activity.startActivity(mapIntent)
            },
            onAnswersClick = onAnswersClick,
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
        HorizontalDivider()
    }
}