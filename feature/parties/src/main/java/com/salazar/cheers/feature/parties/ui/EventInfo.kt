package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.duplexParty
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import com.salazar.cheers.core.util.Utils.conditional
import com.salazar.cheers.core.util.numberFormatter

@Composable
fun PartyInfo(
    modifier: Modifier = Modifier,
    privacy: Privacy? = null,
    price: Int? = null,
    city: String? = null,
    hostId: String? = null,
    address: String? = null,
    hostName: String? = null,
    startDate: Long = 0L,
    endDate: Long = 0L,
    interestedCount: Int = 0,
    goingCount: Int = 0,
    onTicketingClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onPrivacyClick: (() -> Unit)? = null,
    onAnswersClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
    ) {
        if (startDate != 0L) {
//            PartyHeaderItem(
//                icon = Icons.Outlined.CalendarToday,
//                text = com.salazar.cheers.core.util.relativeTimeFormatter(startDate).toString(),
//                modifier = Modifier.padding(16.dp, vertical = 8.dp),
//            )
        }

        if (interestedCount != 0 || goingCount != 0) {
            PartyHeaderItem(
                icon = Icons.Outlined.Group,
                text = "${numberFormatter(value = interestedCount)} interested - ${
                    numberFormatter(value = goingCount)
                } going",
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                onClick = onAnswersClick,
            )
        }

        if (!hostName.isNullOrBlank()) {
            val eventBy = buildAnnotatedString {
                append("Party by ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(hostName)
                }
            }

            PartyHeaderItem(
                icon = Icons.Outlined.Home,
                text = eventBy.text,
                modifier = Modifier
                    .padding(16.dp, vertical = 8.dp)
                    .noRippleClickable {
                        if (hostId != null) {
                            onUserClick(hostId)
                        }
                    },
            )
        }

        if (!address.isNullOrBlank()) {
            PartyHeaderItem(
                icon = Icons.Outlined.LocationOn,
                text = address,
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                onClick = onAddressClick,
            )
        }

        if (!city.isNullOrBlank()) {
            PartyHeaderItem(
                icon = Icons.Outlined.PinDrop,
                text = city,
                modifier = Modifier.padding(16.dp, vertical = 8.dp)
            )
        }

        if (price != null) {
            val text = if ((price ?: 0) <= 0) "Free" else "Price ${price ?: (0 / 100)}"
            PartyHeaderItem(
                icon = Icons.Outlined.Paid,
                text = text,
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                onClick = onTicketingClick,
            )
        }

        if (privacy != null) {
            PartyHeaderItem(
                icon = Icons.Outlined.Public,
                text = "${privacy.title} - ${privacy.subtitle}",
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                onClick = onPrivacyClick,
            )
        }
    }
}

@Composable
fun PartyHeaderItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    if (text.isBlank())
        return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .conditional(onClick != null) {
                clickable {
                    if (onClick != null) {
                        onClick()
                    }
                }
            }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
        )
    }
}

@Preview
@Composable
private fun PartyInfoPreview() {
    val party = duplexParty
    CheersPreview {
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
            onUserClick = { s: String -> },
            onTicketingClick = {},
        )
    }
}
