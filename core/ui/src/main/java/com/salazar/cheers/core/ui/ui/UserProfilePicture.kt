package com.salazar.cheers.core.ui.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.util.Utils.conditional


@Composable
fun UserProfilePicture(
    modifier: Modifier = Modifier,
    picture: String?,
    storyState: StoryState = StoryState.EMPTY,
    placeHolder: Int = R.drawable.default_profile_picture,
    size: Dp = 54.dp,
    onClick: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFA500),
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
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

//    AsyncImage(
//        model = ,
//        contentDescription =
//    )
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = picture)
                .apply(block = fun ImageRequest.Builder.() {
                    transformations(CircleCropTransformation())
                    error(placeHolder)
                    fallback(placeHolder)
                }).build()
        ),
        contentDescription = "Profile picture",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable { onClick() }
        ,
        contentScale = ContentScale.Crop,
    )
}