package com.salazar.cheers.feature.edit_profile.editgender

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
data object EditGenderScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/gender"

fun NavController.navigateToEditGender(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = EditGenderScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.editGenderScreen(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    composable<EditGenderScreen>(
        deepLinks = listOf(
            navDeepLink<EditGenderScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(EditProfileGraph)
        }

        val viewModel: EditProfileViewModel = hiltViewModel(parentEntry)

        EditGenderRoute(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}
