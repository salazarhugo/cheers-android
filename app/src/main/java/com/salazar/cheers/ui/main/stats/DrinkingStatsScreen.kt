package com.salazar.cheers.ui.main.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.data.db.entities.UserStats

@Composable
fun DrinkingStatsScreen(
    userStats: UserStats,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
//        Stat(title = "Number of drinks", value = userStats.drinks.toString())
        Stat(title = "Average drunkenness", value = userStats.avgDrunkenness.toString())
        Stat(title = "Max drunkenness", value = userStats.maxDrunkenness.toString())
        Stat(title = "Favorite drink", value = userStats.favoriteDrink.displayName)
    }
}

@Composable
fun Stat(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
