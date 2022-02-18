package com.salazar.cheers.components.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.perf.metrics.resource.ResourceType
import com.salazar.cheers.R

@Composable
fun UserProfilePicture(
    profilePictureUrl: String,
    @ResourceType placeHolder: Int = R.drawable.default_profile_picture,
    size: Dp = 54.dp,
) {
    Image(
        painter = rememberImagePainter(
            data = profilePictureUrl,
            builder = {
                transformations(CircleCropTransformation())
                error(placeHolder)
            },
        ),
        contentDescription = "Profile picture",
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
