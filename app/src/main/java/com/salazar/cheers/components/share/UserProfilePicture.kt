package com.salazar.cheers.components.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = profilePictureUrl).apply(block = fun ImageRequest.Builder.() {
                transformations(CircleCropTransformation())
                error(placeHolder)
            }).build()
        ),
        contentDescription = "Profile picture",
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
