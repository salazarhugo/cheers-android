package com.salazar.cheers.ui.compose.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.data.user.User
import com.salazar.cheers.ui.main.profile.ProfileButtons

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