package com.salazar.cheers.feature.create_post.drink

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.drink.DrinkXsComponent

@Composable
fun DrinkList(
    drinks: List<Drink>,
    modifier: Modifier = Modifier,
    onClick: (Drink) -> Unit = {},
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
    ) {
        items(
            items = drinks,
        ) { drink ->
            DrinkXsComponent(
                drink = drink,
                modifier = Modifier,
                onClick = {
                    onClick(drink)
                },
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun DrinkListPreview() {
    CheersPreview {
        SelectDrinkScreen(
            drinks = emptyList(),
            modifier = Modifier,
        )
    }
}