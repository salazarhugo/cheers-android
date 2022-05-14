package com.salazar.cheers.ui.main.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun BeverageScreen(
    onBackPressed: () -> Unit,
    onSelectBeverage: (Beverage) -> Unit,
) {
    val drinks = Beverage.values().toList().sortedBy { it.displayName }
        .filter { it.displayName.isNotBlank() }
    val grouped = drinks.groupBy { it.displayName[0] }

    Scaffold(
        topBar = {
            ChooseOnMapAppBar(
                onBackPressed = onBackPressed,
            )
        },
    ) {
        Column {
            Drinks(grouped = grouped, onBeverageClick = onSelectBeverage)
        }
    }
}

@Composable
fun Drinks(
    grouped: Map<Char, List<Beverage>>,
    onBeverageClick: (Beverage) -> Unit,
) {
    LazyColumn {
        grouped.forEach { (initial, drinks) ->
            stickyHeader {
                CharacterHeader(initial)
            }
            items(drinks, key = { it.name }) { drink ->
                Drink(drink = drink, onBeverageClick = onBeverageClick)
            }
        }
        item {
            Text(
                text = "More coming soon",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

@Composable
fun CharacterHeader(initial: Char) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = initial.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun Drink(
    drink: Beverage,
    onBeverageClick: (Beverage) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBeverageClick(drink) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(drink.icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = drink.displayName,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun ChooseOnMapAppBar(
    onBackPressed: () -> Unit,
) {
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        title = {
            Column {
                Text(
                    text = "Choose beverage",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                    fontSize = 14.sp
                )
                Text(
                    text = "Stolichnaya",
                    fontSize = 14.sp
                )
            }
        },
        actions = {
            TextButton(onClick = {
                onBackPressed()
            }) {
                Text("OK")
            }
        },
    )
}
