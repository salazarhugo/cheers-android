package com.salazar.cheers.ui.compose.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.salazar.cheers.data.party.Party
import com.salazar.common.ui.extensions.noRippleClickable

@Composable
fun EventInfo(
    party: Party,
    onTicketingClick: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    party.apply {
        Column {
            EventHeaderItem(
                icon = Icons.Default.Timer,
                text = com.salazar.cheers.core.util.relativeTimeFormatter(startDate).toString(),
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
            )
            EventHeaderItem(
                icon = Icons.Default.Group,
                text = "${numberFormatter(value = interestedCount)} interested - ${
                    numberFormatter(value = goingCount)
                } going",
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
            )
            val eventBy = buildAnnotatedString {
                append("Event by ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(hostName)
                }
            }
            Row(
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Flag, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = eventBy,
                    modifier = Modifier.noRippleClickable {
                        onUserClick(party.hostId)
                    },
                )
            }
            Row(
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.PinDrop, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = locationName,
                    )
                    if (address.isNotBlank())
                        Text(
                            text = address,
                            style = MaterialTheme.typography.labelMedium,
                        )
                }
            }
            val text = if (price <= 0) "Free" else "Price ${price / 100}"
            EventHeaderItem(
                icon = Icons.Default.Paid,
                text = text,
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
                onClick = onTicketingClick,
            )
            EventHeaderItem(
                icon = Icons.Default.Public,
                text = "${privacy.title} - ${privacy.subtitle}",
                modifier = Modifier.padding(16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
fun EventHeaderItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
        )
    }
}
