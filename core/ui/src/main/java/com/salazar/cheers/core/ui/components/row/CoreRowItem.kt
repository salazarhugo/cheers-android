package com.salazar.cheers.core.ui.components.row

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.util.Utils.clickableIf

@Composable
fun CoreRowItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: @Composable () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clickableIf(onClick != null) {
                if (onClick != null) {
                    onClick()
                }
            }
            .then(modifier)
            .semantics(mergeDescendants = true) { },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                    )
                }
            }
        }
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.weight(1f))
            trailingIcon()
        }
    }
}

@Preview
@Composable
private fun CoreRowItemPreview() {
    CheersPreview {
        CoreRowItem(
            modifier = Modifier,
            title = "Add location",
            icon = {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location icon",
                )
            },
            onClick = {},
            trailingIcon = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                    )
                }
            }
        )
    }
}
