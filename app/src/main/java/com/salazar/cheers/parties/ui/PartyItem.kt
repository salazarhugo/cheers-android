package com.salazar.cheers.parties.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.internal.Party
import com.salazar.cheers.ui.main.party.EventItemDetails
import com.salazar.cheers.ui.main.party.EventMutualFriends

@Composable
fun PartyItem(
    modifier: Modifier = Modifier,
    party: Party,
    onEventClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onEventClicked(party.id) },
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AsyncImage(
                model = party.bannerUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
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
        EventItemDetails(
            name = party.name,
            hostName = party.hostName,
            price = party.price,
            startTimeSeconds = party.startDate,
        )
        EventMutualFriends(
            profilePictureUrls = party.mutualProfilePictureUrls,
            usernames = party.mutualUsernames,
            mutualCount = party.mutualCount,
        )
    }
}
