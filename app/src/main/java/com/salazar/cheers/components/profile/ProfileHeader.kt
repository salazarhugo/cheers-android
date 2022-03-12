package com.salazar.cheers.components.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.profile.ProfileStats

@Composable
fun ProfileHeader(
    user: User,
    onStatClicked: (statName: String, username: String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .fillMaxWidth()
    ) {

        Image(
            painter = rememberImagePainter(
                data = user.profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentDescription = null,
        )
        ProfileStats(user, onStatClicked)
        Spacer(Modifier.height(18.dp))
    }
}
