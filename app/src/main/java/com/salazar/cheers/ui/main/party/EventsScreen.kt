package com.salazar.cheers.ui.main.party

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.numberFormatter
import com.salazar.cheers.ui.compose.ChipGroup
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.event.EventDetails
import com.salazar.cheers.ui.compose.event.EventGoingButton
import com.salazar.cheers.ui.compose.event.EventInterestButton
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.main.search.SearchBar

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Party) -> Unit,
    onGoingToggle: (Party) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SearchBar(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        searchInput = uiState.query,
                        onSearchInputChanged = onQueryChange,
                    )
                }
                ChipGroup(
                    users = listOf(
                        "For you",
                        "Local",
                        "This week",
                        "Friends",
                        "Groups",
                        "Online",
                        "Following"
                    )
                )
            }
        },
    ) {
        val parties = uiState.parties

        if (parties == null)
            LoadingScreen()
        else
            SwipeToRefresh(
                onRefresh = {},
                state = rememberSwipeToRefreshState(isRefreshing = false),
                modifier = Modifier.padding(it),
            ) {
                EventList(
                    events = parties,
                    onEventClicked = onEventClicked,
                    onInterestedToggle = onInterestedToggle,
                    onMoreClick = onMoreClick,
                    onGoingToggle = onGoingToggle,
                )
            }
    }
}

@Composable
fun EventList(
    events: List<Party>,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Party) -> Unit,
    onGoingToggle: (Party) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(events, key = { it.id }) { event ->
            if (event != null) {
                Event(
                    party = event,
                    onEventClicked = onEventClicked,
                    onInterestedToggle = onInterestedToggle,
                    onMoreClick = onMoreClick,
                    onGoingToggle = onGoingToggle,
                )
            }
        }
    }
}

@Composable
fun Event(
    party: Party,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Party) -> Unit,
    onGoingToggle: (Party) -> Unit,
    onMoreClick: (String) -> Unit,
    onShareClick: () -> Unit = {},
) {
    Column {
        Box(contentAlignment = Alignment.TopEnd) {
            AsyncImage(
                model = party.bannerUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .aspectRatio(16 / 9f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                placeholder = ColorPainter(Color.LightGray),
                error = ColorPainter(Color.LightGray),
                fallback = ColorPainter(Color.LightGray),
            )
            IconButton(
                onClick = { onMoreClick(party.id) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
        EventDetails(
            name = party.name,
            privacy = party.privacy,
            startTimeSeconds = party.startDate,
            onEventDetailsClick = { onEventClicked(party.id) },
            showArrow = true,
        )
        if (party.locationName.isNotBlank()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.PinDrop, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = party.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Text(
            text = "${numberFormatter(value = party.interestedCount.toInt())} interested - ${
                numberFormatter(
                    value = party.goingCount.toInt()
                )
            } going",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        )

        EventMutualFriends(
            profilePictureUrls = party.mutualProfilePictureUrls,
            usernames = party.mutualUsernames,
            mutualCount = party.mutualCount,
        )

        val uid = remember { FirebaseAuth.getInstance().currentUser?.uid!! }

        if (party.hostId == uid)
            FilledTonalButton(
                onClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
        else
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                EventInterestButton(
                    interested = party.interested,
                    modifier = Modifier.weight(1f),
                    onInterestedToggle = { onInterestedToggle(party) },
                )
                EventGoingButton(
                    going = party.going,
                    modifier = Modifier.weight(1f),
                    onGoingToggle = { onGoingToggle(party) },
                )
            }
    }
}

@Composable
fun EventMutualFriends(
    profilePictureUrls: List<String>,
    usernames: List<String>,
    mutualCount: Int,
) {
    val otherCount = usernames.size - mutualCount
    if (usernames.isNotEmpty())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                if (profilePictureUrls.isNotEmpty())
                    UserProfilePicture(
                        picture = profilePictureUrls[0],
                        size = 26.dp,
                    )
                if (profilePictureUrls.size > 1)
                    UserProfilePicture(
                        modifier = Modifier.offset(x = 13.dp),
                        picture = profilePictureUrls[1],
                        size = 26.dp,
                    )
            }
            if (profilePictureUrls.size > 1)
                Spacer(Modifier.width(21.dp))
            else
                Spacer(Modifier.width(8.dp))
            val text = usernames.joinToString(", ")
            val plurial = if (usernames.size > 1) "are" else "is"
            val end =
                if (otherCount > 0) " and $otherCount ${if (otherCount > 1) "others" else "other"} are going" else " $plurial going"

            Text(
                text = text + end,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
}

