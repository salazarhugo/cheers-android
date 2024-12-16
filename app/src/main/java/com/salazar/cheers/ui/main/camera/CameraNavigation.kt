package com.salazar.cheers.ui.main.camera

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/camera"

@Serializable
data object CameraScreen

fun NavController.navigateToCamera(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = CameraScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.cameraScreen(
    navigateBack: () -> Unit,
) {
    composable<CameraScreen>(
        deepLinks = listOf(
            navDeepLink<CameraScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        CameraRoute(
            navigateBack = navigateBack,
        )
    }
}
