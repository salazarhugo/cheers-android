package com.salazar.cheers.feature.create_post.drink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateDrinkRoute(
    navigateBack: () -> Unit,
    viewModel: CreateDrinkViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateDrinkScreen(
        icons = uiState.icons,
        drinkName = uiState.name,
        navigateBack = navigateBack,
        onDrinkNameChange = viewModel::onNameChange,
        onCreate = {
            viewModel.onCreateDrink(it)
            navigateBack()
        },
    )
}
