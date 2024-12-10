package com.salazar.cheers.core.ui.components.party

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.CheersPreview

@Composable
fun PartyBannerComponent(
    bannerUrl: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = bannerUrl,
            contentDescription = "Party banner",
            modifier = Modifier
                .fillMaxWidth()
                .blur(
                    radius = 150.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .aspectRatio(16 / 9f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
        AsyncImage(
            model = bannerUrl,
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(16 / 9f)
                .clickable {
                    if (onClick != null) {
                        onClick()
                    }
                }
            ,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
    }
}

@Preview
@Composable
private fun PartyBannerComponentPreview() {
    CheersPreview {
        PartyBannerComponent(
            bannerUrl = "",
        )
    }
}