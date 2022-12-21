package com.salazar.cheers.ui.compose.suggestions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ConnectContacts() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.Outlined.ContactPage,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                .padding(12.dp)
        )
        Column {
            Text("Connect Contacts", style = MaterialTheme.typography.bodyMedium)
            Text("Follow people you know", style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Connect")
        }
    }
}

