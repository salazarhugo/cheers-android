package com.salazar.cheers.core.ui.components.select_drink

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.coronaExtraDrink
import com.salazar.cheers.core.model.createDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.drink.DrinkXsComponent

@Composable
fun DrinkList(
    drinks: List<Drink>,
    modifier: Modifier = Modifier,
    onClick: (Drink) -> Unit = {},
    onCreateDrinkClick: () -> Unit = {},
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
    ) {
        item {
            DrinkXsComponent(
                drink = createDrink,
                modifier = Modifier
                    .animateItem(),
                onClick = onCreateDrinkClick,
            )
        }
        items(
            items = drinks,
        ) { drink ->
            DrinkXsComponent(
                drink = drink,
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(0.25f),
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
            drinks = listOf(
                coronaExtraDrink,
                coronaExtraDrink,
                coronaExtraDrink,
                coronaExtraDrink,
            ),
            modifier = Modifier,
        )
    }
}