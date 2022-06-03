package com.salazar.cheers.components.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.Check
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
fun EventGoingButton(
    modifier: Modifier = Modifier,
    going: Boolean,
    onGoingToggle: () -> Unit,
) {
    val iconGoing = if (going) Icons.Rounded.Check else Icons.Outlined.HelpOutline
    if (going)
        FilledTonalButton(
            onClick = onGoingToggle,
            modifier = modifier,
        ) {
            Icon(iconGoing, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Going")
        }
    else
        FilledIconButton(
            onClick = onGoingToggle,
            modifier = modifier,
        ) {
            Row() {
                Icon(iconGoing, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Going")
            }
        }
}
