package com.salazar.cheers.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.data.party.Party

@Composable
fun PartyItem(
    modifier: Modifier = Modifier,
    party: Party,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onPartyClicked(party.id) },
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
        PartyMutualFriends(
            profilePictureUrls = party.mutualProfilePictureUrls,
            usernames = party.mutualUsernames,
            mutualCount = party.mutualCount,
        )
    }
}

@Composable
fun PartyMutualFriends(
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
