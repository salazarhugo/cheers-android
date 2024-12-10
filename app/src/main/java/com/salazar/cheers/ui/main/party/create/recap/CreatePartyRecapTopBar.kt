package com.salazar.cheers.ui.main.party.create.recap

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
internal fun CreatePartyRecapTopBar(
    title: String,
    onDismiss: () -> Unit,
) {
    TopAppBar(
        windowInsets = WindowInsets(0.dp),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon",
                )
            }
        },
    )
}

