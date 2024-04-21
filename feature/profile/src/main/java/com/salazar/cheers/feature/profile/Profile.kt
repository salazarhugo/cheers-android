package com.salazar.cheers.feature.profile

import androidx.compose.runtime.Composable
import com.salazar.cheers.core.model.User

@Composable
fun ProfileItem(
    user: User,
    onStatClicked: () -> Unit,
    onWebsiteClick: (String) -> Unit,
) {
    ProfileHeader(
        user = user,
        onFriendsClick = onStatClicked,
        onWebsiteClick = onWebsiteClick,
    )
}