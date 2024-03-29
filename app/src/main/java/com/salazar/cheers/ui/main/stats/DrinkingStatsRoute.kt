package com.salazar.cheers.ui.main.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.ui.ui.LoadingScreen

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
    val uiState by drinkingStatsViewModel.uiState.collectAsStateWithLifecycle()
    val userStats = uiState.userStats

    if (userStats != null)
        DrinkingStatsScreen(
            userStats = userStats
        )
    else
        LoadingScreen()
}
