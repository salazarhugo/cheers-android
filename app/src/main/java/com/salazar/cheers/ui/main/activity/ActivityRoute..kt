package com.salazar.cheers.ui.main.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

/**
 * Stateful composable that displays the Navigation route for the Activity screen.
 *
 * @param activityViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ActivityRoute(
    appState: CheersAppState,
    activityViewModel: ActivityViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by activityViewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

    ActivityScreen(
        uiState = uiState,
        onBackNav = { navActions.navigateBack() },
        onSwipeRefresh = activityViewModel::onSwipeRefresh,
        onActivityClick = {
            navActions.navigateToOtherProfile(it.username)
//            when (it.type) {
//                ActivityType.FOLLOW -> navActions.navigateToOtherProfile(it.username)
//                ActivityType.POST_LIKE -> navActions.navigateToOtherProfile(it.username)
//                else -> {
//                    navActions.navigateToOtherProfile(it.username)
//                }
//            }
        }
    )
}
