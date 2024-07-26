package com.salazar.cheers.feature.edit_profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.emptyDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.drink.DrinkComponent
import com.salazar.cheers.core.ui.components.favorite_drink.FavoriteDrinkComponent


@Composable
fun EditProfileDrink(
    drink: Drink?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    if (drink != null) {
        FavoriteDrinkComponent(
            drink = drink,
            modifier = modifier,
            onClick = onClick,
        )
    } else {
        IconButton(
            modifier = modifier,
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun EditProfileDrinkPreview() {
    CheersPreview {
        EditProfileDrink(
            drink = emptyDrink,
            modifier = Modifier.padding(16.dp),
        )
    }
}