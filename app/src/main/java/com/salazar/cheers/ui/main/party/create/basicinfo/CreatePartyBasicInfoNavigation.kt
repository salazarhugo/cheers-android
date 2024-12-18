package com.salazar.cheers.ui.main.party.create.basicinfo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.salazar.cheers.ui.main.party.create.CreatePartyGraph
import com.salazar.cheers.ui.main.party.create.CreatePartyViewModel
import kotlinx.serialization.Serializable

@Serializable
data object CreatePartyBasicInfo

fun NavController.navigateToCreatePartyBasicInfo() {
    navigate(CreatePartyBasicInfo)
}

fun NavGraphBuilder.createPartyScreenBasicInfo(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    composable<CreatePartyBasicInfo>(
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(CreatePartyGraph)
        }

        val viewModel: CreatePartyViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CreatePartyBasicInfoScreen(
            uiState = uiState,
            navigateBack = navigateBack,
            onNameChange = viewModel::onNameChange,
            onStartDateChange = viewModel::onStartTimeSecondsChange,
            onEndDateChange = viewModel::onEndTimeSecondsChange,
            onHasEndDateToggle = viewModel::hasEndDateToggle,
            onShowGuestListToggle = viewModel::onShowGuestListToggle,
            onPrivacyChange = viewModel::onPrivacyChange,
        )
    }
}
