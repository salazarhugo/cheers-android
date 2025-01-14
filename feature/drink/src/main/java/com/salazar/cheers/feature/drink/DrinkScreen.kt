package com.salazar.cheers.feature.drink

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.components.drink.DrinkXsComponent
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun DrinkScreen(
    uiState: DrinkUiState,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    val drink = uiState.drink

    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = onBackPressed,
                title = "",
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (drink != null) {
                item {
                    DrinkXsComponent(
                        drink = drink
                    )
                }
            }
        }
    }
}

@Composable
fun DrinkHeader(
    picture: String,
    modifier: Modifier = Modifier,
) {
    val items = listOf(picture)
    val shape = RoundedCornerShape(16.dp)

}

@Preview
@Composable
private fun DrinkScreenPreview() {
    CheersPreview {

    }
}