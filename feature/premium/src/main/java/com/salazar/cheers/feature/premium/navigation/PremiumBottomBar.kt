package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.AnimatedBorder
import com.salazar.cheers.core.ui.modifier.gradientBackground
import com.salazar.cheers.core.ui.theme.nightClubColors

@Composable
fun PremiumBottomBar(
    formattedPrice: String,
    modifier: Modifier = Modifier,
    onSubscribeClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        AnimatedBorder(
            colors = listOf(Color.White) + nightClubColors
        ) {
            Button(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .gradientBackground(0f, nightClubColors),
                onClick = onSubscribeClick,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                )
            ) {
                Text(
                    text = "Subscribe for $formattedPrice",
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun PremiumBottomBarPreview() {
    CheersPreview {
        PremiumBottomBar(
            formattedPrice = "$4.99/month"
        )
    }
}
