package com.salazar.cheers.ui.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.data.user.User
import com.salazar.cheers.ui.main.profile.ProfileStats

@Composable
fun ProfileHeader(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onStoryClick: (String) -> Unit,
    onWebsiteClick: (String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        UserProfilePicture(
            picture = user.picture,
            size = 110.dp,
            storyState = user.storyState,
            onClick = { onStoryClick(user.username) },
        )
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
            style = MaterialTheme.typography.titleMedium.copy(),
        )
    }
}

@Composable
fun ProfileBio(
    bio: String
) {
    if (bio.isBlank())
        return
    Spacer(Modifier.height(4.dp))
    Text(
        text = bio,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
        textAlign = TextAlign.Center,
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
    ClickableText(
        text = AnnotatedString(website),
        style = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Normal
        ),
        onClick = { onClick(website) },
    )
}
