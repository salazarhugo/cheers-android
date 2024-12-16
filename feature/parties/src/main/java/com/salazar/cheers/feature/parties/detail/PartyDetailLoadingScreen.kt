package com.salazar.cheers.feature.parties.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.UserItemLoading
import com.salazar.cheers.core.ui.modifier.ShimmerShape

@Composable
internal fun PartyDetailLoadingScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        ShimmerShape(
            width = 100.dp,
            modifier = Modifier
                .fillMaxWidth()
                .blur(
                    radius = 150.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .aspectRatio(16 / 11f),
        )
        repeat(10) {
            UserItemLoading()
        }
    }
}

@ScreenPreviews
@Composable
private fun PartyDetailLoadingScreenPreview() {
    CheersPreview {
        PartyDetailLoadingScreen()
    }
}