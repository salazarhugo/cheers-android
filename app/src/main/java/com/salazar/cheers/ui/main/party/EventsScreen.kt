package com.salazar.cheers.ui.main.party

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CategoryTitle
import com.salazar.cheers.parties.ui.PartyItem
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.dateTimeFormatter
import com.salazar.cheers.parties.ui.EventsTopBar
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.theme.StrongRed


@Composable
fun EventsScreen(
    uiState: EventsUiState,
    onEventClicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
             EventsTopBar(
                 query = uiState.query,
                 onQueryChange = onQueryChange,
             )
        },
    ) {
        val parties = uiState.parties

        if (parties == null)
            LoadingScreen()
        else
            SwipeToRefresh(
                onRefresh = {},
                state = rememberSwipeToRefreshState(uiState.isLoading),
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            ) {
                EventList(
                    events = parties,
                    onEventClicked = onEventClicked,
                    onMoreClick = onMoreClick,
                )
            }
    }
}

@Composable
fun EventList(
    events: List<Party>,
    onEventClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item {
            CategoryTitle(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.on_display),
            )
        }
        item {
            LazyRow(
            ) {
                items(events, key = { it.id }) { event ->
                    PartyItem(
                        modifier = Modifier.width(260.dp),
                        party = event,
                        onEventClicked = onEventClicked,
                        onMoreClick = onMoreClick,
                    )
                }
            }
        }
//        item {
//            CategoryTitle(
//                modifier = Modifier.padding(16.dp),
//                text = stringResource(id = R.string.on_display),
//            )
//        }
        items(events, key = { it.id }) { event ->
            PartyItem(
                party = event,
                onEventClicked = onEventClicked,
                onMoreClick = onMoreClick,
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

@Composable
fun EventItemDetails(
    name: String,
    hostName: String,
    price: Int,
    startTimeSeconds: Long,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = hostName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = dateTimeFormatter(timestamp = startTimeSeconds),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.error,
                )
                PriceTag(
                    price = price,
                )
            }
        }
        Icon(Icons.Outlined.Star, null)
    }
}

@Composable
fun PriceTag(
    price: Int,
) {
    val text = if (price == 0)
        "Free"
    else
        "${String.format("%.2f", price / 10E2)}€"

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(StrongRed)
            .padding(horizontal = 8.dp),
        color = Color.White,
    )
}