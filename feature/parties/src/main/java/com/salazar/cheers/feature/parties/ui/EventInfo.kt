package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.util.numberFormatter
import com.salazar.cheers.core.model.Party
import com.salazar.common.ui.extensions.noRippleClickable

@Composable
fun PartyInfo(
    party: Party,
    onTicketingClick: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    party.apply {
        Column {
            PartyHeaderItem(
                icon = Icons.Outlined.Timer,
                text = com.salazar.cheers.core.util.relativeTimeFormatter(startDate).toString(),
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
            )

            if (interestedCount != 0 || goingCount != 0) {
                PartyHeaderItem(
                    icon = Icons.Outlined.Group,
                    text = "${numberFormatter(value = interestedCount)} interested - ${
                        numberFormatter(value = goingCount)
                    } going",
                    modifier = Modifier.padding(16.dp, vertical = 8.dp),
                )
            }

            if (hostName.isNotBlank()) {
                val eventBy = buildAnnotatedString {
                    append("Party by ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(hostName)
                    }
                }

                PartyHeaderItem(
                    icon = Icons.Outlined.Flag,
                    text = eventBy.text,
                    modifier = Modifier
                        .padding(16.dp, vertical = 8.dp)
                        .noRippleClickable {
                            onUserClick(party.hostId)
                        },
                )
            }

            PartyHeaderItem(
                icon = Icons.Outlined.PinDrop,
                text = locationName,
                modifier = Modifier.padding(16.dp, vertical = 8.dp)
            )

            if (price != null) {
                val text = if ((price ?: 0) <= 0) "Free" else "Price ${price ?: (0 / 100)}"
                PartyHeaderItem(
                    icon = Icons.Outlined.Paid,
                    text = text,
                    modifier = Modifier.padding(16.dp, vertical = 8.dp),
                    onClick = onTicketingClick,
                )
            }

            PartyHeaderItem(
                icon = Icons.Outlined.Public,
                text = "${privacy.title} - ${privacy.subtitle}",
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
fun PartyHeaderItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {},
) {
    if (text.isBlank())
        return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
        )
    }
}
