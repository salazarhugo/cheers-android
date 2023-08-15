package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.salazar.cheers.data.user.User

@Composable
fun ProfileItem(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onStoryClick: (String) -> Unit,
    onWebsiteClick: (String) -> Unit,
    onEditProfileClicked: () -> Unit,
    onDrinkingStatsClick: (String) -> Unit,
) {
    Column() {
        ProfileHeader(
            user = user,
            isEditable = true,
            onStatClicked = onStatClicked,
            onWebsiteClick = onWebsiteClick,
            onEditProfileClick = onEditProfileClicked,
        )
    }
}