package com.salazar.cheers.core.ui.components.worker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.WorkerState
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun WorkerProgressComponent(
    workerState: WorkerState,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
) {
    if (workerState.isFinished) {
        return
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (workerState) {
                WorkerState.ENQUEUED ->
                    Text(
                        text = "Will automatically post when possible",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )

                WorkerState.RUNNING ->
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp)),
                    )

                else -> Unit
            }
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onCancelClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                )
            }
        }

        HorizontalDivider()
    }
}

@ComponentPreviews
@Composable
private fun WorkerProgressComponentEnqueuedPreview() {
    CheersPreview {
        WorkerProgressComponent(
            workerState = WorkerState.ENQUEUED,
            modifier = Modifier.padding(16.dp),
            onCancelClick = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun WorkerProgressComponentPreview() {
    CheersPreview {
        WorkerProgressComponent(
            workerState = WorkerState.RUNNING,
            modifier = Modifier.padding(16.dp),
            onCancelClick = {},
        )
    }
}