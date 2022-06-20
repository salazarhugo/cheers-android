package com.salazar.cheers.components.share

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.firebase.perf.metrics.resource.ResourceType
import com.salazar.cheers.R
import com.salazar.cheers.util.Utils.conditional

@Composable
fun UserProfilePicture(
    modifier: Modifier = Modifier,
    avatar: String,
    hasStory: Boolean = false,
    @ResourceType placeHolder: Int = R.drawable.default_profile_picture,
    size: Dp = 54.dp,
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
            ImageRequest.Builder(LocalContext.current).data(data = avatar).apply(block = fun ImageRequest.Builder.() {
                transformations(CircleCropTransformation())
                error(placeHolder)
            }).build()
        ),
        contentDescription = "Profile picture",
        modifier = modifier
            .conditional(hasStory) {
                border(border, CircleShape)
            }
            .size(size)
            .conditional(hasStory) {
                padding(5.dp)
            }
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
