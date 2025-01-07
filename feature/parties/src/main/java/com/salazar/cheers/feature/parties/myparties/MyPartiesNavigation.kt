package com.salazar.cheers.feature.parties.myparties

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object MyPartiesScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/myparties"

fun NavController.navigateToMyParties(
    navOptions: NavOptions? = null,
) {
    navigate(
        route = MyPartiesScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.myPartiesScreen(
    navigateBack: () -> Unit,
    navigateToPartyDetail: (String) -> Unit,
    navigateToPartyMoreSheet: (String) -> Unit,
    navigateToTickets: () -> Unit,
    navigateToCreateParty: () -> Unit,
) {
    composable<MyPartiesScreen>(
        deepLinks = listOf(
            navDeepLink<MyPartiesScreen>(basePath = DEEP_LINK_URI_PATTERN),
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
    ) {
        MyPartiesRoute(
            navigateToPartyDetail = navigateToPartyDetail,
            navigateToPartyMoreSheet = navigateToPartyMoreSheet,
            navigateToTickets = navigateToTickets,
            navigateToCreateParty = navigateToCreateParty,
            onChangeCityClick = {},
            navigateBack = navigateBack,
        )
    }
}
