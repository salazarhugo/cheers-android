package com.salazar.cheers.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ListItem
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PostBottomSheet(
    sheetState: ModalBottomSheetState,
    onSettingsClick: () -> Unit = {},
    onCopyProfileUrlClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetContent = {
            Column( horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline)
                )
                Item(sheetState, text = "Link", icon = Icons.Outlined.Link)
                Item(sheetState, text = "Share", icon = Icons.Outlined.Share)
                Item(sheetState, text = "Report", icon = Icons.Outlined.Report)
                Item(sheetState, text = "Unfollow", icon = Icons.Outlined.UnfoldLess)
            }
        }
    ) {
        content()
    }
}

@Composable
fun Item(
    sheetState: ModalBottomSheetState,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    sheetState.hide()
                    onClick()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
        )
        Spacer(Modifier.width(22.dp))
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
