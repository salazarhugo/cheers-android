package com.salazar.cheers.compose.profile

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.profile.ProfileStats

@Composable
fun ProfileHeader(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onStoryClick: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        UserProfilePicture(
            avatar = user.profilePictureUrl,
            size = 80.dp,
            storyState = user.storyState,
            onClick = { onStoryClick(user.username) }
        )
        ProfileStats(user, onStatClicked)
        Spacer(Modifier.height(18.dp))
    }
}
