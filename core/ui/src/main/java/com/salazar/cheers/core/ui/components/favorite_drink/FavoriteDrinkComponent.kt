package com.salazar.cheers.core.ui.components.favorite_drink

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
    onClick: () -> Unit = {},
) {
    // TODO Favourite Drink Component
    PostDrink(
        drink = drink.name,
        picture = drink.icon,
        modifier = modifier,
        onClick = onClick,
    )
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
