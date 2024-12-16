package com.salazar.cheers.feature.home.select_city

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.City
import com.salazar.cheers.core.model.parisCity
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState

@Composable
fun SelectCityScreen(
    isNearbyEnabled: Boolean,
    currentCity: String,
    cities: List<City>,
    modifier: Modifier = Modifier,
    onClick: (City) -> Unit = {},
    onToggleNearby: (Boolean) -> Unit = {},
) {
    val state = rememberRefreshLayoutState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CheersSearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                searchInput = "",
                onSearchInputChanged = {
                },
                placeholder = {
                    Text("Search city")
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
            )
        },
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = {},
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            CityList(
                isNearbyEnabled = isNearbyEnabled,
                currentCity = currentCity,
                cities = cities,
                modifier = Modifier,
                onClick = onClick,
                onToggleNearby = onToggleNearby,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SelectCityScreenPreview() {
    CheersPreview {
        SelectCityScreen(
            cities = listOf(
                parisCity
            ),
            currentCity = "Paris",
            isNearbyEnabled = true,
            modifier = Modifier,
        )
    }
}