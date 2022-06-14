package com.salazar.cheers.ui.main.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.event.EventDetails
import com.salazar.cheers.components.event.EventGoingButton
import com.salazar.cheers.components.event.EventInterestButton
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.UserProfilePicture
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.numberFormatter
import com.salazar.cheers.ui.main.search.SearchBar

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    events: LazyPagingItems<Event>,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onGoingToggle: (Event) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Column() {
                SearchBar(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    searchInput = uiState.query,
                    onSearchInputChanged = onQueryChange,
                )
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
        SwipeToRefresh(
            onRefresh = { events.refresh() },
            state = rememberSwipeToRefreshState(isRefreshing = false),
            modifier = Modifier.padding(it),
        ) {
            EventList(
                events = events,
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
    events: LazyPagingItems<Event>,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onGoingToggle: (Event) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(events, key = { it.id }) { event ->
            if (event != null) {
                Event(
                    event = event,
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
    event: Event,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onGoingToggle: (Event) -> Unit,
    onMoreClick: (String) -> Unit,
    onShareClick: () -> Unit = {},
) {
    Column {
        Box(contentAlignment = Alignment.TopEnd) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
            IconButton(
                onClick = { onMoreClick(event.id) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
        EventDetails(
            name = event.name,
            privacy = event.privacy,
            startTimeSeconds = event.startDate,
            onEventDetailsClick = { onEventClicked(event.id) },
            showArrow = true,
        )
        if (event.locationName.isNotBlank()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.PinDrop, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = event.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Text(
            text = "${numberFormatter(value = event.interestedCount)} interested - ${
                numberFormatter(
                    value = event.goingCount
                )
            } going",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        )

        EventMutualFriends(
            profilePictureUrls = event.mutualProfilePictureUrls,
            usernames = event.mutualUsernames,
            mutualCount = event.mutualCount,
        )

        val uid = remember { FirebaseAuth.getInstance().currentUser?.uid!! }

        if (event.hostId == uid)
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
                    interested = event.interested,
                    modifier = Modifier.weight(1f),
                    onInterestedToggle = { onInterestedToggle(event) },
                )
                EventGoingButton(
                    going = event.going,
                    modifier = Modifier.weight(1f),
                    onGoingToggle = { onGoingToggle(event) },
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
            Box() {
                if (profilePictureUrls.isNotEmpty())
                    UserProfilePicture(
                        profilePictureUrl = profilePictureUrls[0],
                        size = 26.dp,
                    )
                if (profilePictureUrls.size > 1)
                    UserProfilePicture(
                        modifier = Modifier.offset(x = 13.dp),
                        profilePictureUrl = profilePictureUrls[1],
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

