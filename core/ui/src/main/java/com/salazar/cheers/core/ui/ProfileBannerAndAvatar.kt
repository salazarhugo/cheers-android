package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun ProfileBannerAndAvatar(
    picture: String?,
    banner: String?,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomStart,
    ) {
        Column {
            ProfileBanner(
                banner = banner,
            )
            Row(
                modifier = Modifier
                    .heightIn(32.dp, 64.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End,
                content = content,
            )
        }
        UserProfilePicture(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape),
            picture = picture,
            size = 110.dp,
//            storyState = user.storyState,
            onClick = {},
        )
    }
}
