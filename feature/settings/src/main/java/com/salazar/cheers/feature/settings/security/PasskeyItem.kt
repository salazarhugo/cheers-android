package com.salazar.cheers.feature.settings.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Credential
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.util.dateTimeFormatter
import java.util.Date

@Composable
fun PasskeyItem(
    credential: Credential,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val deviceText = "Stored on your ${credential.deviceName}"
    val lastUsedText = "Last used ${dateTimeFormatter(timestamp = credential.lastUsed)}"

    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
            )
            Column() {
                Text(
                    text = credential.name,
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = deviceText,
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = lastUsedText,
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}


@Preview
@Composable
private fun PasskeyItemPreview() {
    CheersPreview {
        PasskeyItem(
            credential = Credential(
                name = "Passkey #1",
                deviceName = "Pixel 8 Pro",
                lastUsed = Date().time,
            ),
        )
    }
}