package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.ShimmerShape
import com.salazar.cheers.core.ui.modifier.cheersShimmer

@Composable
fun PartyItemLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        AsyncImage(
            model = "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .aspectRatio(16 / 9f)
                .clip(MaterialTheme.shapes.medium)
                .cheersShimmer(isLoading = true),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray),
            fallback = ColorPainter(Color.LightGray),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ShimmerShape(
                width = 100.dp,
                height = 12.dp,
            )
            ShimmerShape(
                width = 120.dp,
                height = 12.dp,
            )
            ShimmerShape(
                width = 170.dp,
                height = 12.dp,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun PartyComponentLoadingPreview() {
    CheersPreview {
        PartyItemLoading()
    }
}