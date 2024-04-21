package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent

@Composable
fun PostDrink(
    drink: String,
    picture: String,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    isPremium: Boolean = false,
    onClick: () -> Unit = {},
) {
    if (drink.isBlank() || picture.isBlank())
        return

    val color = borderColor
        ?: when (isPremium) {
            true -> MaterialTheme.colorScheme.surfaceTint
            false -> MaterialTheme.colorScheme.outline
        }

    Row(
        modifier = modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .border(width = 2.dp, color = color, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InspectionAwareComponent(
            modifier = Modifier.size(24.dp),
            inspectionModePainter = R.drawable.beer,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(picture)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                modifier = Modifier.size(22.dp),
                contentDescription = "Label",
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = drink,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}


@ComponentPreviews
@Composable
fun PostDrinkBeerPreview() {
    val lightBlue = Color(0xFFADD8E6)
    val blue = Color(0xFF43A6C6)
    val green = Color(0xFF008000)
    val greenYellow = Color(0xFF00FF00)
    val yellow = Color(0xFFFFFF00)
    val orange = Color(0xFFFFA500)
    val red = Color(0xFFFF0000)

    val colors = listOf(
        null,
        lightBlue,
        blue,
        green,
        greenYellow,
        yellow,
        orange,
        red,
        Color(0xFFBF40BF),
        Color(0xFFDC2367)
    )

    CheersPreview(
        modifier = Modifier.padding(16.dp),
        maxItemsInEachRow = 1,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        colors.forEach {
            PostDrink(
                drink = "Beer",
                picture = "wf",
                borderColor = it,
            )
        }
    }
}