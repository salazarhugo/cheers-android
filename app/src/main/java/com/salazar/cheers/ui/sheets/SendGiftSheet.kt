package com.salazar.cheers.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.salazar.cheers.components.DividerM3

@Composable
fun SendGiftSheet(
    name: String,
    onStickerClick: () -> Unit,
    bottomSheetNavigator: BottomSheetNavigator,
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

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        DividerM3()
        Item(
            text = "Fontaine",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Shot",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Pint",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Bottle",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Magnum",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Methuselah",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Salmanazar",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
        Item(
            text = "Balthazar",
            icon = Icons.Outlined.Redeem,
            onClick = onStickerClick
        )
    }
}

@Composable
fun Item(
    text: String,
    icon: ImageVector,
    price: Int = 50,
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
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(22.dp))
        Column() {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$price coins",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
