package com.salazar.cheers.core.ui.components.premium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onSubscribeClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier,
        onClick = {},
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Cheers Premium",
                style = MaterialTheme.typography.headlineMedium,
            )
            Button(
                onClick = onSubscribeClick,
            ) {
                Text(
                    text = "Subscribe",
                )
            }
        }
    }
}

@Preview
@Composable
private fun PremiumCardPreview() {
    CheersPreview {
        PremiumCard(
            modifier = Modifier.padding(16.dp),
        )
    }
}