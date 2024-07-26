package com.salazar.cheers.feature.settings.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.theme.GreenGoogle

@Composable
fun SignInMethodItem(
    method: String,
    icon: ImageVector,
    linked: Boolean,
    unlinkable: Boolean = false,
    onClick: () -> Unit = {},
    onUnlink: () -> Unit,
    onLink: () -> Unit = {},
) {
    SettingItem(
        title = method,
        icon = icon,
        onClick = onClick,
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (linked) {
                    if (unlinkable) {
                        Text(
                            modifier = Modifier.clickable { onUnlink() },
                            text = "Unlink",
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GreenGoogle,
                    )
                } else {
                    TextButton(
                        onClick = { onLink() }
                    ) {
                        Text(
                            text = "Link",
                        )
                    }
                }
            }
        }
    )
}
