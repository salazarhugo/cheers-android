package com.salazar.cheers.feature.create_post.adddrink

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun AddDrinkBottomBar(
    coinsBalance: Int,
    modifier: Modifier = Modifier,
    onRechargeClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (coinsBalance > 0) {
            Text(
                text= "Balance: $coinsBalance",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        FilledTonalButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            onClick = onRechargeClick,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "Recharge",
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AddDrinkBottomBarPreview() {
    CheersPreview {
        AddDrinkBottomBar(
            coinsBalance = 2141,
            onRechargeClick = {},
        )
    }
}
