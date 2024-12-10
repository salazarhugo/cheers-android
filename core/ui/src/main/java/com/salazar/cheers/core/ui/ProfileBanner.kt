package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.util.Utils.clickableIf

@Composable
fun ProfileBanner(
    modifier: Modifier = Modifier,
    banner: String? = null,
    clickable: Boolean = false,
    alpha: Float = 1f,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopEnd,
    ) {
        AsyncImage(
            modifier = Modifier
                .aspectRatio(3f)
                .alpha((alpha - 0.7f) / 0.3f)
                .blur(
                    radius = 50.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                ),
            model = banner,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        AsyncImage(
            modifier = Modifier
                .aspectRatio(3f)
                .alpha((alpha - 0.7f) / 0.3f)
                .background(Color.DarkGray)
                .clickableIf(clickable) {
                    onClick()
                },
            model = banner,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        if (clickable) {
            FilledTonalIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun ProfileBannerPreview() {
    CheersPreview {
        ProfileBanner(
            modifier = Modifier.padding(16.dp),
            clickable = true,
        )
    }
}