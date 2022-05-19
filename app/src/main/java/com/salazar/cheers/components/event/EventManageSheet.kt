package com.salazar.cheers.components.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.main.chat.SymbolAnnotationType
import com.salazar.cheers.ui.main.chat.messageFormatter
import com.salazar.cheers.ui.main.profile.Item
import com.salazar.cheers.ui.main.profile.ProfileSheetUIAction


@Composable
fun EventManageSheet(
    onEditClick: () -> Unit,
    onCopyLink: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
        Item(
            text = "Edit",
            icon = Icons.Outlined.Edit,
            onClick = onEditClick,
        )
        Item(
            text = "Copy link",
            icon = Icons.Outlined.Link,
            onClick = onCopyLink,
        )
        Item(
            text = "Save",
            icon = Icons.Outlined.Save,
            onClick = {}
        )
    }
}

