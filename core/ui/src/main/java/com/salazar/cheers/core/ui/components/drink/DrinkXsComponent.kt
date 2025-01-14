package com.salazar.cheers.core.ui.components.drink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Rarity
import com.salazar.cheers.core.model.coronaExtraDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.coins.CoinsComponent
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent
import com.salazar.cheers.core.ui.modifier.gradientBackground
import com.salazar.cheers.core.ui.modifier.leftBorder

@Composable
fun DrinkXsComponent(
    drink: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val price = drink.price
    val color = try {
        if (drink.rarity == Rarity.DEFAULT) Color.Transparent
        else Color(android.graphics.Color.parseColor(drink.color))
    } catch (e: Exception) {
        val lightBlue = Color(0xFFADD8E6)
        lightBlue
    }

    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .leftBorder(
                    width = 6.dp,
                    color = color,
                )
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .gradientBackground(
                    -90f, colors = listOf(
                        MaterialTheme.colorScheme.outline,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                ),
            tonalElevation = 4.dp,
            color = Color.Transparent,
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                InspectionAwareComponent(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .aspectRatio(1f),
                    inspectionModePainter = R.drawable.beer,
                ) {
                    if (drink.id == "add") {
                        Icon(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp),
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(drink.icon)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .height(50.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val offset = Offset(0.0f, 3.0f)
            Text(
                text = drink.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
            if (price > 0) {
                CoinsComponent(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    price = price,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun DrinkXsComponentPreview() {
    CheersPreview {
        DrinkXsComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            drink = coronaExtraDrink,
        )
    }
}