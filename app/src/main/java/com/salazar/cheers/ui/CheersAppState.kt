package com.salazar.cheers.ui

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun rememberCheersAppState(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberAnimatedNavController(bottomSheetNavigator),
    cheersNavigationActions: CheersNavigationActions = CheersNavigationActions(navController),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) = remember(snackBarHostState, bottomSheetNavigator, navController, snackbarScope, context) {
    CheersAppState(
        snackBarHostState,
        bottomSheetNavigator,
        navController,
        cheersNavigationActions,
        snackbarScope,
        context,
    )
}

class CheersAppState(
    val snackBarHostState: SnackbarHostState,
    val bottomSheetNavigator: BottomSheetNavigator,
    val navController: NavHostController,
    val navActions: CheersNavigationActions,
    val snackbarScope: CoroutineScope,
    private val context: Context
) {
    fun navigateBack() {
        navController.popBackStack()
    }

    fun showSnackBar(message: String) {
        snackbarScope.launch {
            snackBarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
            )
        }
    }
}