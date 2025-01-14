package com.salazar.cheers.feature.create_post.recap

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.salazar.cheers.feature.create_post.CreatePostGraph
import com.salazar.cheers.feature.create_post.CreatePostRoute
import com.salazar.cheers.feature.create_post.CreatePostViewModel
import kotlinx.serialization.Serializable

@Serializable
data object CreatePostRecap

fun NavController.navigateToCreatePostRecap() {
    navigate(CreatePostRecap)
}

fun NavGraphBuilder.createPostScreenRecap(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToAddDrink: () -> Unit,
    navigateToAddPeople: () -> Unit,
    navigateToAddLocation: () -> Unit,
    navigateToMoreOptions: () -> Unit,
    navigateToCreateDrink: () -> Unit,
) {
    composable<CreatePostRecap>(
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
            navController.getBackStackEntry(CreatePostGraph)
        }

        val viewModel: CreatePostViewModel = hiltViewModel(parentEntry)

        CreatePostRoute(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToCamera = navigateToCamera,
            navigateToMoreOptions = navigateToMoreOptions,
            navigateToAddPeople = navigateToAddPeople,
            navigateToAddLocation = navigateToAddLocation,
            navigateToCreateDrink = navigateToCreateDrink,
            navigateToAddDrink = navigateToAddDrink,
        )
    }
}
