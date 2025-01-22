package com.salazar.cheers.feature.create_post.adddrink

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.create_post.CreatePostGraph
import com.salazar.cheers.feature.create_post.CreatePostViewModel
import kotlinx.serialization.Serializable

@Serializable
data object CreatePostAddDrinkScreen

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/post/create"

fun NavController.navigateToCreatePostAddDrink(
    navOptions: NavOptions? = null,
) {
    navigate(
        route = CreatePostAddDrinkScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.createPostAddDrinkScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToCreateDrink: () -> Unit,
    onRewardedAdClick: () -> Unit,
) {
    composable<CreatePostAddDrinkScreen>(
        deepLinks = listOf(
            navDeepLink<CreatePostAddDrinkScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
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
            navController.getBackStackEntry(CreatePostGraph)
        }

        val viewModel: CreatePostViewModel = hiltViewModel(parentEntry)

        CreatePostAddDrinkRoute(
            navigateBack = navigateBack,
            viewModel = viewModel,
            onCreateDrinkClick = navigateToCreateDrink,
            onRewardedAdClick = onRewardedAdClick,
        )
    }
}
