package com.salazar.cheers.feature.home.select_city

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.model.City
import com.salazar.cheers.core.model.parisCity
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun CityList(
    isNearbyEnabled: Boolean,
    currentCity: String,
    cities: List<City>,
    modifier: Modifier = Modifier,
    onClick: (City) -> Unit = {},
    onToggleNearby: (Boolean) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            NearbyCityComponent(
                currentCity = currentCity,
                checked = isNearbyEnabled,
                onClick = onToggleNearby,
            )
        }
        items(
            items = cities,
        ) { city ->
            val selected = when (isNearbyEnabled) {
                true -> false
                false -> city.name == currentCity
            }

            CityComponent(
                city = city,
                selected = selected,
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                ,
                onClick = {
                    onClick(city)
                },
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun CityListPreview() {
    CheersPreview {
        SelectCityScreen(
            isNearbyEnabled = true,
            currentCity = "Paris",
            cities = listOf(parisCity),
            modifier = Modifier,
        )
    }
}