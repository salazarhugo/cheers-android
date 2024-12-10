package com.salazar.cheers.feature.parties.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.modifier.ShimmerShape
import kotlin.random.Random

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ShimmerShape(
                width = 54.dp,
                height = 54.dp,
                shape = CircleShape,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(2) {
                    val width = remember {
                        Random.nextInt(50, 200).dp
                    }
                    ShimmerShape(
                        width = width,
                        height = 12.dp,
                    )
                }
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