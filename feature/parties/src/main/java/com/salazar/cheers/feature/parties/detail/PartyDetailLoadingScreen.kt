package com.salazar.cheers.feature.parties.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.modifier.ShimmerShape
import com.salazar.cheers.feature.parties.ui.PartyDetailsLoading
import kotlin.random.Random

@Composable
internal fun PartyDetailLoadingScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
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
            ShimmerShape(
                width = 100.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(16 / 9f),
            )
        }
        PartyDetailsLoading(
            modifier = Modifier.padding(16.dp),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(30) {
                val width = Random.nextInt(50, 200).dp
                ShimmerShape(
                    width = width,
                    height = 12.dp,
                )
            }
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