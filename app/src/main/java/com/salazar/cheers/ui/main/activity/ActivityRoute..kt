package com.salazar.cheers.ui.main.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Activity screen.
 *
 * @param activityViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ActivityRoute(
    activityViewModel: ActivityViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by activityViewModel.uiState.collectAsState()

    ActivityScreen(
        uiState = uiState,
        onBackNav = { navActions.navigateBack() },
        onSwipeRefresh = activityViewModel::onSwipeRefresh,
        onActivityClick = {
            when(it.type) {
                ActivityType.FOLLOW -> navActions.navigateToOtherProfile(it.userId)
//                ActivityType.POST_LIKE -> navActions.navigateToOtherProfile(it.userId)
                else -> {}
            }
        }
    )
}
