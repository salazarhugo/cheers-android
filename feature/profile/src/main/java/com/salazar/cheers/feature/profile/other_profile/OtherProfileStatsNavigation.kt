package com.salazar.cheers.feature.profile.other_profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.salazar.cheers.core.model.UserID
import kotlinx.serialization.Serializable

@Serializable
data class OtherProfileStatsScreen(
    val otherUserID: UserID,
    val username: String,
    val verified: Boolean,
)

fun NavController.navigateToOtherProfileStats(
    otherUserID: UserID,
    username: String,
    verified: Boolean,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = OtherProfileStatsScreen(
            otherUserID = otherUserID,
            username = username,
            verified = verified,
        ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.otherProfileStatsScreen(
    navigateToOtherProfile: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    composable<OtherProfileStatsScreen>(
    ) {
        OtherProfileStatsRoute(
            navigateToOtherProfile = navigateToOtherProfile,
            navigateBack = navigateBack,
        )
    }
}