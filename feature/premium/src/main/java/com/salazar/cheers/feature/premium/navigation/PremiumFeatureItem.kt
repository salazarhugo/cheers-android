package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.gradientBackground
import com.salazar.cheers.core.ui.theme.nightClubColors

@Composable
fun PremiumFeatureItem(
    name: String,
    description: String,
    icon: ImageVector? = Icons.Default.AdsClick,
    colors: List<Color> = nightClubColors,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
        ),
        headlineContent = {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        supportingContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                )
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Chevron icon",
                )
            }
        },
        leadingContent = {
            val size = 30.dp
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(MaterialTheme.shapes.medium)
                    .gradientBackground(0f, colors)
                ,
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon ?: Icons.Default.AdsClick,
                    contentDescription = "Feature icon",
                    tint = Color.White,
                )
            }
        },
    )
}

@ComponentPreviews
@Composable
private fun PremiumScreenPreview() {
    CheersPreview {
        PremiumFeatureItem(
            name = "No Ads",
            description = "No more ads in your feed where Cheers sometimes shows ads.",
        )
    }
}
