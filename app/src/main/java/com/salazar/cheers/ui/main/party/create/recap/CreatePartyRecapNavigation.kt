package com.salazar.cheers.ui.main.party.create.recap

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.salazar.cheers.ui.main.party.create.CreatePartyGraph
import com.salazar.cheers.ui.main.party.create.CreatePartyViewModel
import kotlinx.serialization.Serializable

@Serializable
data object CreatePartyRecap

fun NavController.navigateToCreatePartyRecap() {
    navigate(CreatePartyRecap)
}

fun NavGraphBuilder.createPartyScreenRecap(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToBasicInfo: () -> Unit,
    navigateToLocation: () -> Unit,
    navigateToDescription: () -> Unit,
    navigateToUserProfile: (String) -> Unit,
) {
    composable<CreatePartyRecap>(
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
        }
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(CreatePartyGraph)
        }

        val viewModel: CreatePartyViewModel = hiltViewModel(parentEntry)

        CreatePartyRecapStateful(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToBasicInfo = navigateToBasicInfo,
            navigateToLocation = navigateToLocation,
            navigateToDescription = navigateToDescription,
            navigateToUserProfile = navigateToUserProfile,
        )
    }
}
