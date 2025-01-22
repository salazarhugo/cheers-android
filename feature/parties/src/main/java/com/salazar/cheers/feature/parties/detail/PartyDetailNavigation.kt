package com.salazar.cheers.feature.parties.detail

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.ui.navigation.PartyDetailScreen
import com.salazar.cheers.core.util.Constants

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/party/{partyID}"

fun NavController.navigateToPartyDetail(
    partyID: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = PartyDetailScreen(partyID = partyID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.partyDetailScreen(
    navigateToMap: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToTicketing: (String) -> Unit,
    navigateToEditParty: (String) -> Unit,
    navigateToGuestList: (String) -> Unit,
) {
    composable<PartyDetailScreen>(
        deepLinks = listOf(
            navDeepLink<PartyDetailScreen>(basePath = DEEP_LINK_URI_PATTERN),
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
        PartyDetailRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToEditParty = navigateToEditParty,
            navigateToGuestList = navigateToGuestList,
            navigateToMap = navigateToMap,
            navigateToTicketing = navigateToTicketing,
        )
    }
}
