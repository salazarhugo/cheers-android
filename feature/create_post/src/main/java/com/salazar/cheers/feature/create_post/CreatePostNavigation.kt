package com.salazar.cheers.feature.create_post

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.salazar.cheers.feature.create_post.adddrink.createPostAddDrinkScreen
import com.salazar.cheers.feature.create_post.adddrink.navigateToCreatePostAddDrink
import com.salazar.cheers.feature.create_post.addlocation.createPostAddLocationScreen
import com.salazar.cheers.feature.create_post.addlocation.navigateToCreatePostAddLocation
import com.salazar.cheers.feature.create_post.addpeople.createPostAddPeopleScreen
import com.salazar.cheers.feature.create_post.addpeople.navigateToCreatePostAddPeople
import com.salazar.cheers.feature.create_post.drink.navigateToCreateDrink
import com.salazar.cheers.feature.create_post.moreoptions.createPostMoreOptionsScreen
import com.salazar.cheers.feature.create_post.moreoptions.navigateToCreatePostMoreOptions
import com.salazar.cheers.feature.create_post.recap.CreatePostRecap
import com.salazar.cheers.feature.create_post.recap.createPostScreenRecap
import kotlinx.serialization.Serializable

@Serializable
data object CreatePostGraph

fun NavController.navigateToCreatePost(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = CreatePostGraph,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.createPostGraph(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    navigation<CreatePostGraph>(
        startDestination = CreatePostRecap,
    ) {
        createPostScreenRecap(
            navController = navController,
            navigateBack = navigateBack,
            navigateToMoreOptions = navController::navigateToCreatePostMoreOptions,
            navigateToAddLocation = navController::navigateToCreatePostAddLocation,
            navigateToAddPeople = navController::navigateToCreatePostAddPeople,
            navigateToCreateDrink = navController::navigateToCreateDrink,
            navigateToCamera = {},
            navigateToAddDrink = navController::navigateToCreatePostAddDrink,
        )
        createPostAddDrinkScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToCreateDrink = navController::navigateToCreateDrink,
        )
        createPostAddPeopleScreen(
            navController = navController,
            navigateBack = navigateBack,
        )
        createPostAddLocationScreen(
            navController = navController,
            navigateBack = navigateBack,
        )
        createPostMoreOptionsScreen(
            navController = navController,
            navigateBack = navigateBack,
        )
    }
}
