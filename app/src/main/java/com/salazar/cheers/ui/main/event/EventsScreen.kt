package com.salazar.cheers.ui.main.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.event.EventDetails
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.numberFormatter
import com.salazar.cheers.ui.main.search.SearchBar
import com.salazar.cheers.ui.theme.Typography

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    events: LazyPagingItems<Event>,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Event) -> Unit,
    onQueryChange: (String) -> Unit,
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
                    users = listOf("For you", "Local", "This week", "Friends", "Groups", "Online", "Following")
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
            )
        }
    }
}

@Composable
fun EventList(
    events: LazyPagingItems<Event>,
    onEventClicked: (String) -> Unit,
    onInterestedToggle: (Event) -> Unit,
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
) {
    Column {
        AsyncImage(
            model = event.imageUrl,
            contentDescription =null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
        EventDetails(
            name = event.name,
            privacy = event.privacy,
            startTimeSeconds = event.startDate,
            onEventDetailsClick = { onEventClicked(event.id) },
        )
        if (event.locationName.isNotBlank())
            Text(text = event.locationName, style = Typography.labelSmall)
        Text(
            text = "${numberFormatter(value = event.interestedCount)} interested - ${
                numberFormatter(
                    value = event.goingCount
                )
            } going",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        )

        val icon = if (event.interested) Icons.Rounded.Star else Icons.Rounded.StarBorder

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            FilledTonalButton(
                onClick = { onInterestedToggle(event) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Interested")
            }
            FilledTonalButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Going")
            }
        }
    }
}

