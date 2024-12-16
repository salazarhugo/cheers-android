package com.salazar.cheers.feature.home.select_city

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews


@Composable
fun SelectCityBottomSheet(
    viewModel: SelectCityViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cities = uiState.cities

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        SelectCityScreen(
            isNearbyEnabled = uiState.isNearbyEnabled,
            currentCity = uiState.currentCity,
            cities = cities,
            modifier = Modifier,
            onClick = viewModel::onCityClick,
            onToggleNearby = viewModel::onToggleNearby,
        )
    }
}


@ScreenPreviews
@Composable
private fun SelectCityBottomSheetPreview() {
    CheersPreview {
        SelectCityBottomSheet(
            modifier = Modifier,
        )
    }
}