package com.salazar.cheers.core.ui.components.drink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.emptyDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent

@Composable
fun DrinkComponent(
    drink: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InspectionAwareComponent(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .size(50.dp),
                inspectionModePainter = R.drawable.beer,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drink.icon)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .size(40.dp)
                    ,
                )
            }
            Text(
                text = drink.name,
            )
        }
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun DrinkComponentPreview() {
    CheersPreview {
        DrinkComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            drink = emptyDrink,
        )
    }
}