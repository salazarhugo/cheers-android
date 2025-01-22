package com.salazar.cheers.feature.create_post.adddrink

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.components.coins.RechargeCoinsBottomSheet
import com.salazar.cheers.core.ui.components.select_drink.DrinksViewModel
import com.salazar.cheers.core.ui.components.select_drink.SelectDrinkScreen
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.create_post.CreatePostViewModel

@Composable
fun CreatePostAddDrinkRoute(
    navigateBack: () -> Unit,
    onCreateDrinkClick: () -> Unit,
    viewModel: CreatePostViewModel,
    drinksViewModel: DrinksViewModel = hiltViewModel(),
    onRewardedAdClick: () -> Unit,
) {
    val uiState2 by drinksViewModel.uiState.collectAsStateWithLifecycle()
    var showRechargeBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Toolbar(
                title = "Add drink",
                onBackPressed = navigateBack,
            )
        },
        bottomBar = {
            AddDrinkBottomBar(
                coinsBalance = uiState2.coinsBalance,
                modifier = Modifier
                    .navigationBarsPadding(),
                onRechargeClick = {
                    showRechargeBottomSheet = true
                }
            )
        }
    ) {
        SelectDrinkScreen(
            searchInput = uiState2.searchInput,
            drinks = uiState2.drinks,
            modifier = Modifier.padding(it),
            onClick = {
                if (it.price > uiState2.coinsBalance) {
                    showRechargeBottomSheet = true
                    return@SelectDrinkScreen
                }
                viewModel.selectDrink(it)
                navigateBack()
            },
            onCreateDrinkClick = onCreateDrinkClick,
            onSearchInputChanged = drinksViewModel::onSearchInputChanged,
        )
    }

    if (showRechargeBottomSheet) {
        RechargeCoinsBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showRechargeBottomSheet = false
            },
            onRewardedAdClick = onRewardedAdClick,
        )
    }
}
