package com.salazar.cheers.ui.compose.share

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.salazar.cheers.data.enums.StoryState
import com.salazar.cheers.ui.compose.animations.Bounce


@Composable
fun UserProfilePicture(
    modifier: Modifier = Modifier,
    picture: String?,
    storyState: StoryState = StoryState.EMPTY,
    @ResourceType placeHolder: Int = R.drawable.default_profile_picture,
    size: Dp = 54.dp,
    onClick: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFA500),
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val border =
        when (storyState) {
            StoryState.LOADING -> BorderStroke(
                2.dp,
                color = color
            )
            StoryState.EMPTY -> BorderStroke(
                0.dp,
                color = Color.Transparent
            )
            StoryState.SEEN ->
                BorderStroke(
                    1.dp,
                    color = Color.LightGray
                )
            StoryState.NOT_SEEN ->
                BorderStroke(
                    2.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEC01FB),
                            Color(0xFF7f00ff),
                        )
                    )
                )
            StoryState.UNKNOWN -> TODO()
        }

    Bounce(onBounce = onClick) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = picture)
                    .apply(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                        error(placeHolder)
                    }).build()
            ),
            contentDescription = "Profile picture",
            modifier = modifier
                .border(border, CircleShape)
                .padding(5.dp)
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}