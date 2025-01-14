package com.salazar.cheers.core.ui.components.select_drink

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState

@Composable
fun SelectDrinkScreen(
    searchInput: String = "",
    drinks: List<Drink>,
    modifier: Modifier = Modifier,
    onClick: (Drink) -> Unit = {},
    onCreateDrinkClick: () -> Unit = {},
    onSearchInputChanged: (String) -> Unit = {},
) {
    val state = rememberRefreshLayoutState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CheersSearchBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                ,
                searchInput = searchInput,
                onSearchInputChanged = onSearchInputChanged,
                placeholder = {
                    Text("Search drink")
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
            )
        },
    ) { insets ->
        Column(
            modifier = Modifier
                .padding(insets),
        ) {
            DrinkList(
                drinks = drinks,
                modifier = Modifier,
                onClick = onClick,
                onCreateDrinkClick = onCreateDrinkClick,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SelectDrinkScreenPreview() {
    CheersPreview {
        SelectDrinkScreen(
            drinks = listOf(
                Drink(
                    id = String(),
                    name = "Heineiken",
                    icon = String(),
                ),
                Drink(
                    id = String(),
                    name = "1664",
                    icon = String(),
                ),
            ),
            modifier = Modifier,
        )
    }
}