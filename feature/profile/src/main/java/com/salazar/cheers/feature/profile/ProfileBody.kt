package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.ui.CheersPreview

@Composable
fun ProfileBody(
    user: User,
    onFriendsClick: () -> Unit,
    onWebsiteClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        ProfileBio(
            bio = user.bio,
        )
        ProfileWebsite(
            website = user.website,
            onClick = onWebsiteClick,
        )
//        MultiAvatarComponent(
//            avatars = listOf(),
//        ) {
//            Text(
//                modifier = Modifier.clickable { onFriendsClick() },
//                text = "${user.friendsCount} Friends",
//            )
//        }
        ProfileStats(
            user = user,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onFriendsClick = onFriendsClick,
        )
        Spacer(Modifier.height(8.dp))
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
        style = MaterialTheme.typography.bodyLarge,
//        fontWeight = FontWeight.W700,
    )
}

@Composable
fun ProfileWebsite(
    website: String,
    onClick: (String) -> Unit,
) {
    if (website.isBlank())
        return
    Spacer(Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(ButtonDefaults.IconSize),
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
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

@Preview
@Composable
private fun ProfileBodyPreview() {
    CheersPreview {
        ProfileBody(
            user = cheersUser,
            onFriendsClick = {},
            onWebsiteClick = {},
        )
    }
}