package com.salazar.cheers.components.event

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.sp
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.dateTimeFormatter
import com.salazar.cheers.internal.numberFormatter
import com.salazar.cheers.internal.relativeTimeFormatter

@Composable
fun EventInfo(
    hostName: String,
    privacy: Privacy,
    price: Int,
    startTimeSeconds: Long,
    interestedCount: Int,
    goingCount: Int,
) {
    Column() {
        EventHeaderItem(
            icon = Icons.Default.Timer,
            text = relativeTimeFormatter(startTimeSeconds).toString(),
            modifier = Modifier.padding(16.dp, vertical = 8.dp),
        )
        EventHeaderItem(
            icon = Icons.Default.Group,
            text = "${numberFormatter(value = interestedCount)} interested - ${
                numberFormatter(value = goingCount)} going",
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
            )
        }
        val text = if (price <= 0) "Free" else "Price ${price/100}"
        EventHeaderItem(
            icon = Icons.Default.Paid,
            text = text,
            modifier = Modifier.padding(16.dp, vertical = 8.dp),
        )
        EventHeaderItem(
            icon = Icons.Default.Public,
            text = "${privacy.title} - ${privacy.subtitle}",
            modifier = Modifier.padding(16.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun EventHeaderItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
        )
    }
}
