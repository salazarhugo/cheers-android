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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.theme.GreySheet


@Composable
fun SelectDrinkBottomSheet(
    drinks: List<Drink>,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onClick: (Drink) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0, WindowInsets.statusBars.getTop(LocalDensity.current),0,0),
    ) {
        SelectDrinkScreen(
            drinks = drinks,
            modifier = Modifier,
            onClick = {
                onClick(it)
                onDismiss()
            },
        )
    }
}


@ScreenPreviews
@Composable
private fun SelectDrinkBottomSheetPreview() {
    CheersPreview {
        SelectDrinkBottomSheet(
            drinks = listOf(
                Drink(
                    id = String(),
                    name = "Heineiken",
                    icon = String(),
                    category = String(),
                ),
                Drink(
                    id = String(),
                    name = "1664",
                    icon = String(),
                    category = String(),
                ),
            ),
            modifier = Modifier,
        )
    }
}