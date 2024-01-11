package com.salazar.cheers.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun ProfileBannerAndAvatar(
    modifier: Modifier = Modifier,
    fraction: Float = 1f,
    picture: String?,
    banner: String?,
    username: String,
    verified: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    val padding = 12 * fraction + 8

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
    ) {
        Column {
            ProfileBanner(
                banner = banner,
                alpha = fraction,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                content = content,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            UserProfilePicture(
                modifier = Modifier
                    .padding(padding.dp)
                    .border((fraction * 5).dp, MaterialTheme.colorScheme.background, CircleShape),
                picture = picture,
                size = (fraction * 70 + 40).dp,
                onClick = {},
            )
            Username(
                modifier = Modifier.alpha(-1 * fraction + 1),
                username = username,
                verified = verified,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                ),
            )
        }
    }
}
