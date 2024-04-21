package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.EventItemDetails
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.model.Party

@Composable
fun PartyItem(
    party: Party,
    modifier: Modifier = Modifier,
    onPartyClicked: (String) -> Unit = {},
    onMoreClick: (String) -> Unit = {},
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
        MutualFriendsComponent(
            modifier = Modifier.padding(horizontal = 16.dp),
            users = party.mutualGoing,
        )
    }
}

@ComponentPreviews
@Composable
fun PartyComponentPreview() {
    CheersPreview {
        PartyItem(
            party = Party(
                name = "Mirage presents Serum w/ Sara Bluma, Aaron Julian, Gianni",
                mutualGoing = mapOf("esf" to "cheers", "afw" to "mcdo", "wf" to "nike"),
            ),
        )
    }
}