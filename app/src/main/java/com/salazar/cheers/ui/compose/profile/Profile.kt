package com.salazar.cheers.ui.compose.profile

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.main.profile.ProfileButtons
import com.salazar.cheers.ui.main.profile.ProfileStats

@Composable
fun ProfileItem(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onStoryClick: (String) -> Unit,
    onWebsiteClick: (String) -> Unit,
    onEditProfileClicked: () -> Unit,
    onDrinkingStatsClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        ProfileHeader(
            user = user,
            onStatClicked = onStatClicked,
            onStoryClick = onStoryClick,
            onWebsiteClick = onWebsiteClick,
        )
        ProfileButtons(
            onEditProfileClicked = onEditProfileClicked,
            onDrinkingStatsClick = { onDrinkingStatsClick(user.username) },
        )
    }
}