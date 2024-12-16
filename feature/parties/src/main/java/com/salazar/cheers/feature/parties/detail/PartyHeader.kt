package com.salazar.cheers.feature.parties.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.core.ui.components.party.PartyBannerComponent
import com.salazar.cheers.feature.parties.ui.PartyDetails
import com.salazar.cheers.feature.parties.ui.PartyHeaderButtons
import com.salazar.cheers.feature.parties.ui.PartyInfo
import com.salazar.cheers.shared.util.LocalActivity

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
            address = party.address.ifBlank { party.locationName },
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
                Text(
                    text = "About",
                )
            }
            FilledTonalButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Discussion",
                )
            }
        }
    }
}