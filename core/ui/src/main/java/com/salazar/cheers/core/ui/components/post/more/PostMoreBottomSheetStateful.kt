package com.salazar.cheers.core.ui.components.post.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.GreySheet

@Composable
fun PostMoreBottomSheetStateful(
    isAuthor: Boolean,
    onDetailsClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Item(
            text = "Details",
            icon = Icons.Outlined.OpenInNew,
            onClick = onDetailsClick,
        )
        Item(
            text = "Link",
            icon = Icons.Outlined.Link,
//            onClick = onLinkClick,
        )
        if (isAuthor) {
            Item(
                text = "Delete",
                icon = Icons.Outlined.Delete,
                onClick = onDeleteClick,
            )
        }
        Item(
            text = "Share",
            icon = Icons.Outlined.Share,
//            onClick = onShareClick,
        )
        Item(
            text = "Report",
            icon = Icons.Outlined.Report,
//            onClick = onReportClick,
        )
        Item(
            text = "Block",
            icon = Icons.Outlined.Block,
//            onClick = onBlockClick,
        )
        if (!isAuthor) {
            Item(
                text = "Unfollow",
                icon = Icons.Outlined.UnfoldLess,
//                onClick = onUnfollow,
            )
        }
    }
}


@Composable
private fun Item(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}