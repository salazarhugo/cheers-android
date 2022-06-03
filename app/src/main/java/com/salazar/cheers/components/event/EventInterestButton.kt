package com.salazar.cheers.components.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.main.chat.SymbolAnnotationType
import com.salazar.cheers.ui.main.chat.messageFormatter


@Composable
fun EventInterestButton(
    modifier: Modifier = Modifier,
    interested: Boolean,
    onInterestedToggle: () -> Unit,
) {
    val icon = if (interested) Icons.Rounded.Star else Icons.Rounded.StarBorder

    FilledTonalButton(
        onClick = onInterestedToggle,
        modifier = modifier,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Interested")
    }
}
