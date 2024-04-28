package com.salazar.cheers.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.dialogs.AvatarDialog

@Composable
fun ProfileBannerAndAvatar(
    banner: String?,
    avatar: String?,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onBannerClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        AvatarDialog(
            avatar = avatar,
            onDismissRequest = {
                openAlertDialog = false
            }
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
    ) {
        Column {
            ProfileBanner(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp)),
                banner = banner,
                onClick = onBannerClick,
                clickable = isEditable,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(69.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
        AvatarComponent(
            avatar = avatar,
            modifier = Modifier
                .border(5.dp, MaterialTheme.colorScheme.background, CircleShape),
            size = 110.dp,
            onClick = {
                if (isEditable.not()) {
                    openAlertDialog = true
                }
                onAvatarClick()
            },
        )
    }
}

@Preview
@Composable
private fun ProfileBannerAndAvatarPreview() {
    CheersPreview {
        ProfileBannerAndAvatar(
            modifier = Modifier.padding(16.dp),
            avatar = "",
            banner = "",
            content = {
                CheersOutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Follow")
                }
            }
        )
    }
}