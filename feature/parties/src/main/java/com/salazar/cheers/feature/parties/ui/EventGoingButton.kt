package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.WatchStatus


@Composable
fun PartyWatchStatusButton(
    modifier: Modifier = Modifier,
    watchStatus: WatchStatus,
    onGoingToggle: () -> Unit,
) {
    val iconGoing = if (watchStatus == WatchStatus.GOING) Icons.Rounded.Check else Icons.Outlined.HelpOutline

    FilledTonalButton(
        onClick = onGoingToggle,
        modifier = modifier,
    ) {
        Icon(iconGoing, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text = watchStatus.name,
        )
    }
}
