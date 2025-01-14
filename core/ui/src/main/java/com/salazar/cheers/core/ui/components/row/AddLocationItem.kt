package com.salazar.cheers.core.ui.components.row

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddLocationItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CoreRowItem(
        modifier = modifier,
        title = "Add location",
        icon = {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Location icon",
            )
        },
        onClick = onClick,
        trailingIcon = {
            IconButton(
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            }
        }
    )
}

