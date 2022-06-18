package com.salazar.cheers.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.profile.ProfileStats
import com.salazar.cheers.util.Utils.conditional

@Composable
fun ProfileHeader(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onStoryClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .fillMaxWidth()
    ) {
        val border = BorderStroke(
            2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFEC01FB),
                    Color(0xFF7f00ff),
                )
            )
        )

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = user.profilePictureUrl)
                    .apply(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    }).build()
            ),
            modifier = Modifier
                .clickable {
                    if (user.hasStory)
                        onStoryClick()
                }
                .conditional(user.hasStory) {
                    border(border, CircleShape)
                }
                .size(80.dp)
                .conditional(user.hasStory) {
                    padding(5.dp)
                }
                .clip(CircleShape),
            contentDescription = null,
        )
        ProfileStats(user, onStatClicked)
        Spacer(Modifier.height(18.dp))
    }
}

