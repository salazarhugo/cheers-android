package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun PartyWatchStatusButton(
    modifier: Modifier = Modifier,
    watchStatus: WatchStatus,
    onGoingToggle: () -> Unit,
    onInterestedClick: () -> Unit,
    onGoingClick: () -> Unit,
) {
    val iconGoing =
        if (watchStatus == WatchStatus.GOING) {
            Icons.Rounded.Check
        } else {
            Icons.Filled.Star
        }

    if (watchStatus == WatchStatus.UNWATCHED) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onInterestedClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = "Star icon",
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Interested",
                )
            }
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = onGoingClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FactCheck,
                    contentDescription = "Help icon",
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Going",
                )
            }
        }
    } else {
        FilledTonalButton(
            onClick = onGoingToggle,
            modifier = modifier,
        ) {
            Icon(
                imageVector = iconGoing,
                contentDescription = "Check icon",
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = watchStatus.name,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun PartyWatchStatusButtonPreview() {
    val status =  WatchStatus.entries
    CheersPreview(
        modifier = Modifier.padding(16.dp),
    ) {
        status.forEach {
            PartyWatchStatusButton(
                modifier = Modifier.fillMaxWidth(),
                watchStatus = it,
                onGoingToggle = {},
                onInterestedClick = {},
                onGoingClick = {},
            )
        }
    }
}
