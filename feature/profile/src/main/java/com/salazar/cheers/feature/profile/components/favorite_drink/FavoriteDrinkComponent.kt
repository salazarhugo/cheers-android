package com.salazar.cheers.feature.profile.components.favorite_drink

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.coronaExtraDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.post.PostDrink


@Composable
fun FavoriteDrinkComponent(
    drink: Drink,
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.outline

    PostDrink(
        drink = drink.name,
        picture = drink.icon,
        modifier = modifier,
    )
//    Card(
//        modifier = modifier,
//        shape = RoundedCornerShape(16.dp),
//        border = BorderStroke(width = 1.dp, color = color)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//        ) {
//            PrettyImage(
//                data = drink.icon,
//                modifier = Modifier.clip(CircleShape),
//            )
//            Text(
//                text = drink.name,
//                style = MaterialTheme.typography.titleLarge,
//            )
//        }
//    }
}

@ComponentPreviews
@Composable
private fun FavoriteDrinkComponentPreview() {
    CheersPreview {
        FavoriteDrinkComponent(
            drink = coronaExtraDrink,
            modifier = Modifier.padding(16.dp),
        )
    }
}
