package com.salazar.cheers.feature.edit_profile.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.edit_profile.EditProfileRoute
import com.salazar.cheers.feature.edit_profile.EditProfileViewModel
import com.salazar.cheers.feature.edit_profile.editgender.editGenderScreen
import com.salazar.cheers.feature.edit_profile.editgender.navigateToEditGender
import com.salazar.cheers.feature.edit_profile.editjob.editJobScreen
import com.salazar.cheers.feature.edit_profile.editjob.navigateToEditJob
import kotlinx.serialization.Serializable

@Serializable
data object EditProfileGraph

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/edit_profile"

fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) {
    this.navigate(
        route = EditProfileGraph,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.editProfileGraph(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    navigation<EditProfileGraph>(
        startDestination = EditProfileRecapScreen,
    ) {
        editProfileRecapScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToGender = navController::navigateToEditGender,
            navigateToJob = navController::navigateToEditJob,
        )

        editGenderScreen(
            navController = navController,
            navigateBack = navigateBack,
        )

        editJobScreen(
            navController = navController,
            navigateBack = navigateBack,
        )
    }
}

@Serializable
data object EditProfileRecapScreen

fun NavGraphBuilder.editProfileRecapScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToGender: () -> Unit,
    navigateToJob: () -> Unit,
) {
    composable<EditProfileRecapScreen> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(EditProfileGraph)
        }

        val viewModel: EditProfileViewModel = hiltViewModel(parentEntry)

        EditProfileRoute(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToGender = navigateToGender,
            navigateToJob = navigateToJob,
        )
    }
}
