package com.salazar.cheers.core.ui.components.avatar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.clickableNullable
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.theme.CheersBlueSecondary
import com.salazar.cheers.core.ui.theme.nightClubColors
import java.util.Locale

@Composable
fun AvatarComponent(
    avatar: String?,
    name: String? = null,
    username: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    size: Dp = 54.dp,
    placeHolder: Int = R.drawable.default_profile_picture,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onClick: (() -> Unit)? = null,
) {
    val modifier = modifier
        .size(size)
        .clip(shape)
        .clickableNullable(onClick)
        .background(backgroundColor)

    Box(
        modifier = modifier,
    ) {
        if (avatar.isNullOrBlank()) {
            InitialsAvatarComponent(
                name = name.orEmpty().ifBlank { username }.orEmpty(),
                size = size,
                modifier = Modifier.matchParentSize(),
                backgroundColor = BlueCheers,
            )
        } else {
            val painter = ImageRequest.Builder(LocalContext.current)
                .data(avatar)
                .error(placeHolder)
                .decoderFactory(SvgDecoder.Factory())
                .fallback(placeHolder)
                .crossfade(true)
                .build()
            AsyncImage(
                model = painter,
                modifier = Modifier.matchParentSize(),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                error = painterResource(id = placeHolder),
                placeholder = painterResource(id = placeHolder),
            )
        }
    }
}

@Composable
private fun InitialsAvatarComponent(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    backgroundColor: Color = CheersBlueSecondary,
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier.clip(CircleShape)
    ) {
        drawCircle(
            brush = Brush.linearGradient(colors = nightClubColors),
            radius = size.toPx() / 2f
        )

        // Extract initials
        val initials = name.takeIf { it.isNotBlank() }?.let {
            it.substring(0, minOf(2, it.length)).uppercase(Locale.ROOT)
        } ?: ""

        val measuredText =
            textMeasurer.measure(
                text = initials,
                overflow = TextOverflow.Clip,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.toPx() / 3f).toSp(),
                    textAlign = TextAlign.Center,
                ),
            )

        drawText(
            textLayoutResult = measuredText,
            topLeft = Offset(
                x = size.toPx() / 2f - measuredText.size.width / 2f,
                y = size.toPx() / 2f - measuredText.size.height / 2f,
            ),
            shadow = Shadow(blurRadius = 10f, offset = Offset(x = 4f, y = 4f)),
        )
    }
}

@ComponentPreviews
@Composable
private fun AvatarComponentPreview() {
    CheersPreview {
        AvatarComponent(
            avatar = "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50?s=200",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun AvatarComponentPreviewInitials() {
    CheersPreview {
        AvatarComponent(
            avatar = null,
            name = "Hugo Lars",
            modifier = Modifier.padding(16.dp),
        )
    }
}
