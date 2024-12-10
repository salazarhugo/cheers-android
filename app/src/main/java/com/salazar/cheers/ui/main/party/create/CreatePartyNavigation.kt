package com.salazar.cheers.ui.main.party.create

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.salazar.cheers.feature.profile.navigation.navigateToOtherProfile
import com.salazar.cheers.ui.main.party.create.basicinfo.createPartyScreenBasicInfo
import com.salazar.cheers.ui.main.party.create.basicinfo.navigateToCreatePartyBasicInfo
import com.salazar.cheers.ui.main.party.create.description.createPartyScreenDescription
import com.salazar.cheers.ui.main.party.create.description.navigateToCreatePartyDescription
import com.salazar.cheers.ui.main.party.create.location.createPartyScreenLocation
import com.salazar.cheers.ui.main.party.create.location.navigateToCreatePartyLocation
import com.salazar.cheers.ui.main.party.create.recap.CreatePartyRecap
import com.salazar.cheers.ui.main.party.create.recap.createPartyScreenRecap
import kotlinx.serialization.Serializable

@Serializable
data object CreatePartyGraph

fun NavController.navigateToCreateParty() {
    navigate(CreatePartyGraph)
}

fun NavGraphBuilder.createPartyGraph(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    navigation<CreatePartyGraph>(
        startDestination = CreatePartyRecap,
    ) {
        createPartyScreenRecap(
            navController = navController,
            navigateBack = navigateBack,
            navigateToBasicInfo = navController::navigateToCreatePartyBasicInfo,
            navigateToLocation = navController::navigateToCreatePartyLocation,
            navigateToDescription = navController::navigateToCreatePartyDescription,
            navigateToUserProfile = navController::navigateToOtherProfile,
        )
        createPartyScreenBasicInfo(
            navController = navController,
            navigateBack = navigateBack,
        )
        createPartyScreenLocation(
            navController = navController,
            navigateBack = navigateBack,
        )
        createPartyScreenDescription(
            navController = navController,
            navigateBack = navigateBack,
        )
    }
}
