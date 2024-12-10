package com.salazar.cheers.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.ShimmerShape
import kotlin.random.Random

@Composable
fun UserItemLoading(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

@ComponentPreviews
@Composable
private fun UserItemLoadingPreview() {
    CheersPreview {
        UserItemLoading(
            modifier = Modifier,
        )
    }
}
