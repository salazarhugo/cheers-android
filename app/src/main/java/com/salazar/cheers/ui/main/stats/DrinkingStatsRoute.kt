package com.salazar.cheers.ui.main.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the DrinkingStats screen.
 *
 * @param drinkingStatsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun DrinkingStatsRoute(
    drinkingStatsViewModel: DrinkingStatsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by drinkingStatsViewModel.uiState.collectAsState()
    val userStats = uiState.userStats

    if (userStats != null)
        DrinkingStatsScreen(
            userStats = userStats
        )
    else
        LoadingScreen()
}
