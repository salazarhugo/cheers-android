package com.salazar.cheers.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ProfileBannerAndAvatar
import com.salazar.cheers.data.user.User

@Composable
fun ProfileHeader(
    user: User,
    isEditable: Boolean,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onWebsiteClick: (String) -> Unit,
    onEditProfileClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        ProfileBannerAndAvatar(
            banner = user.banner,
            picture = user.picture,
            content = {
                if (isEditable)
                    OutlinedButton(
                        onClick = onEditProfileClick,
                    ) {
                        Text(
                            text = "Edit Profile",
                        )
                    }
                else
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null)
                    }
            }
        )
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            ProfileName(
                name = user.name,
                isBusinessAccount = user.isBusinessAccount,
            )
            ProfileBio(
                bio = user.bio,
            )
            ProfileWebsite(
                website = user.website,
                onClick = onWebsiteClick,
            )
            ProfileStats(
                user = user,
                onStatClicked = onStatClicked,
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}


@Composable
fun ProfileName(
    name: String,
    isBusinessAccount: Boolean,
) {
    if (name.isBlank())
        return
    Spacer(Modifier.height(4.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isBusinessAccount) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
fun ProfileBio(
    bio: String
) {
    if (bio.isBlank())
        return
    Spacer(Modifier.height(8.dp))
    Text(
        text = bio,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
    )
}

@Composable
fun ProfileWebsite(
    website: String,
    onClick: (String) -> Unit,
) {
    if (website.isBlank())
        return
    Spacer(Modifier.height(4.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(ButtonDefaults.IconSize),
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceVariant,
        )
        ClickableText(
            text = AnnotatedString(website),
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Normal
            ),
            onClick = { onClick(website) },
        )
    }
}
