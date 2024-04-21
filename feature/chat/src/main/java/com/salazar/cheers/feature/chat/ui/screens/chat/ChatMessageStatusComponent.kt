package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.model.ChatMessageStatus

@Composable
fun ChatMessageStatus(
    status: ChatMessageStatus,
    modifier: Modifier = Modifier,
) {
    when (status) {
        ChatMessageStatus.EMPTY -> {}
        ChatMessageStatus.SCHEDULED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        ChatMessageStatus.SENT -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        ChatMessageStatus.DELIVERED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        ChatMessageStatus.READ -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = BlueCheers,
            )
        }

        ChatMessageStatus.FAILED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Outlined.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }

        ChatMessageStatus.UNRECOGNIZED -> {}
    }
}


@ComponentPreviews
@Composable
private fun ChatMessageStatusPreview() {
    val statuses = ChatMessageStatus.entries

    CheersPreview(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
    ) {
        statuses.forEach {
            ChatMessageStatus(
                modifier = Modifier.padding(16.dp),
                status = it,
            )
        }
    }
}