package com.salazar.cheers.ui.compose.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.feature.profile.SheetItem


@Composable
fun EventManageSheet(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onCopyLink: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
        SheetItem(
            text = "Edit",
            icon = Icons.Outlined.Edit,
            onClick = onEditClick,
        )
        SheetItem(
            text = "Delete",
            icon = Icons.Outlined.Delete,
            onClick = onDeleteClick,
        )
        SheetItem(
            text = "Copy link",
            icon = Icons.Outlined.Link,
            onClick = onCopyLink,
        )
        SheetItem(
            text = "Save",
            icon = Icons.Outlined.Save,
            onClick = {}
        )
    }
}

