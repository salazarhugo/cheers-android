package com.salazar.cheers.feature.premium.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.shared.util.LocalActivity
import kotlinx.coroutines.launch

@Composable
fun PremiumRoute(
    viewModel: PremiumViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {},
    navigateToWelcomeCheersPremium: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val state = rememberRefreshLayoutState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val success = uiState.success

    LaunchedEffect(success) {
        if (!success) return@LaunchedEffect
        viewModel.updateSuccess(false)
        navigateToWelcomeCheersPremium()
    }

    LaunchedEffect(uiState.isRefreshing) {
        if (!uiState.isRefreshing) {
            scope.launch {
                state.finishRefresh(true)
            }
        }
    }

    PremiumScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onSubscribeClick = {
            viewModel.onSubscribeClick(activity)
        },
        onPlanClick = viewModel::onPlanClick,
    )
}
