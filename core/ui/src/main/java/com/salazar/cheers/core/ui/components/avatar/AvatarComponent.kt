package com.salazar.cheers.core.ui.components.avatar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent

@Composable
fun AvatarComponent(
    avatar: String?,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    placeHolder: Int = R.drawable.default_profile_picture,
    onClick: () -> Unit = {},
) {
    val painter = ImageRequest.Builder(LocalContext.current)
        .data(avatar)
        .error(placeHolder)
        .fallback(placeHolder)
        .crossfade(true)
        .build()

    val modifier = modifier
        .size(size)
        .clip(CircleShape)
        .clickable { onClick() }

    InspectionAwareComponent(
        modifier = modifier,
        inspectionModePainter = placeHolder,
    ) {
        AsyncImage(
            model = painter,
            modifier = modifier,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            error = painterResource(id = placeHolder),
            placeholder = painterResource(id = placeHolder),
        )
    }
}

@ComponentPreviews
@Composable
private fun AvatarComponentPreview() {
    CheersPreview {
        AvatarComponent(
            avatar = "",
            modifier = Modifier.padding(16.dp),
        )
    }
}
