package com.salazar.cheers.ui

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.salazar.cheers.navigation.CheersNavigationActions


@Composable
fun rememberCheersAppState(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberAnimatedNavController(bottomSheetNavigator),
    cheersNavigationActions: CheersNavigationActions = CheersNavigationActions(navController),
    context: Context = LocalContext.current
) = remember(snackBarHostState, bottomSheetNavigator, navController, context) {
    CheersAppState(snackBarHostState, bottomSheetNavigator, navController, cheersNavigationActions, context)
}

class CheersAppState(
    val snackBarHostState: SnackbarHostState,
    val bottomSheetNavigator: BottomSheetNavigator,
    val navController: NavHostController,
    val navActions: CheersNavigationActions,
    private val context: Context
) {
    fun navigateBack() {
        navController.popBackStack()
    }
}