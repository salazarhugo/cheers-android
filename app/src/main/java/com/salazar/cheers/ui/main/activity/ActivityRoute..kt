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
        onActivityUIAction = { action ->
            when(action) {
                is ActivityUIAction.OnActivityClick -> navActions.navigateToOtherProfile(action.activity.username)
                ActivityUIAction.OnBackPressed -> navActions.navigateBack()
                ActivityUIAction.OnFriendRequestsClick -> navActions.navigateToFriendRequests()
                ActivityUIAction.OnSwipeRefresh -> activityViewModel.onSwipeRefresh()
            }
        },
    )
}
