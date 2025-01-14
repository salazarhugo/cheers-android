package com.salazar.cheers.core.ui.components.select_drink

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.theme.GreySheet


@Composable
fun SelectDrinkBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: DrinksViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onClick: (Drink) -> Unit = {},
    onCreateDrinkClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drinks = uiState.drinks

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
        contentWindowInsets = {
            WindowInsets(
                0,
                WindowInsets.statusBars.getTop(LocalDensity.current),
                0,
                0
            )
        },
    ) {
        SelectDrinkScreen(
            searchInput = uiState.searchInput,
            drinks = drinks,
            modifier = Modifier,
            onClick = {
                onClick(it)
                onDismiss()
            },
            onCreateDrinkClick = onCreateDrinkClick,
            onSearchInputChanged = viewModel::onSearchInputChanged,
        )
    }
}


@ScreenPreviews
@Composable
private fun SelectDrinkBottomSheetPreview() {
    CheersPreview {
        SelectDrinkBottomSheet(
            modifier = Modifier,
        )
    }
}