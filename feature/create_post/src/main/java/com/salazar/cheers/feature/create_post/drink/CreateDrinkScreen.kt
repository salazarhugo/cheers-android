package com.salazar.cheers.feature.create_post.drink

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.coronaExtraDrink
import com.salazar.cheers.core.model.emptyDrink
import com.salazar.cheers.core.ui.CarouselDrinks
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.CheersOutlinedTextField
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun CreateDrinkScreen(
    drinkName: String,
    icons: List<String>,
    onCreate: (String?) -> Unit,
    navigateBack: () -> Unit,
    onDrinkNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drinks = icons.map {
        emptyDrink.copy(name = drinkName, icon = it)
    }
    val pagerState = rememberPagerState { drinks.count() }

    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = "Create drink",
                onBackPressed = navigateBack,
                actions = {
                    Button(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                        onClick = {
                            val icon = drinks.getOrNull(pagerState.currentPage)?.icon
                            onCreate(icon)
                        },
                    ) {
                        Text("Create")
                    }
                }
            )
        },
        bottomBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            CheersOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = {
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                value = drinkName,
                onValueChange = onDrinkNameChange,
            )
            CarouselDrinks(
                modifier = Modifier.padding(vertical = 16.dp),
                pagerState = pagerState,
                drinks = drinks,
                onBeverageClick = {},
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun CreateDrinkScreenPreview() {
    CheersPreview {
        CreateDrinkScreen(
            icons = listOf("", ""),
            drinkName = coronaExtraDrink.name,
            modifier = Modifier,
            navigateBack = {},
            onDrinkNameChange = {},
            onCreate = {},
        )
    }
}