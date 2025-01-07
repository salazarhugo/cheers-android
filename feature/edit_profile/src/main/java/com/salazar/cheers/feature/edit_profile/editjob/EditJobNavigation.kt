package com.salazar.cheers.feature.edit_profile.editjob

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.edit_profile.EditProfileViewModel
import com.salazar.cheers.feature.edit_profile.navigation.EditProfileGraph
import kotlinx.serialization.Serializable

@Serializable
data object EditJobScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/edit/job"

fun NavController.navigateToEditJob(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = EditJobScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.editJobScreen(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    composable<EditJobScreen>(
        deepLinks = listOf(
            navDeepLink<EditJobScreen>(basePath = DEEP_LINK_URI_PATTERN),
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
            navController.getBackStackEntry(EditProfileGraph)
        }

        val viewModel: EditProfileViewModel = hiltViewModel(parentEntry)

        EditJobRoute(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}
