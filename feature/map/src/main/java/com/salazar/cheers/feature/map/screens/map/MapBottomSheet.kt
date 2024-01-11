package com.salazar.cheers.feature.map.screens.map

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.map.domain.models.UserLocation
import com.salazar.cheers.feature.map.ui.dialogs.UserMapDialog

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MapBottomSheet(
    state: SheetState,
    type: MapAnnotationType,
    modifier: Modifier = Modifier,
    userLocation: UserLocation? = null,
    onDismissRequest: () -> Unit = {},
    onMapUIAction: (MapUIAction) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        scrimColor = Color.Transparent,
        sheetState = state,
        windowInsets = WindowInsets(0,0,0,0),
    ) {
        when(type) {
            MapAnnotationType.POST -> {
//                PostMapDialog(
//                    uiState = uiState,
//                )
            }
            MapAnnotationType.USER -> {
                UserMapDialog(
                    userLocation = userLocation,
                    onClose = onDismissRequest,
                    onChatClick = {
                        onMapUIAction(MapUIAction.OnChatClick(it))
                    }
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun MapBottomSheetPreview() {
    CheersPreview {
        MapBottomSheet(
            state = rememberModalBottomSheetState(),
            type = MapAnnotationType.USER,
            userLocation = UserLocation(
                id = "",
                picture = "",
                username = "cheers",
                verified = true,
                name = "Cheers Social",
                locationName = "Aulnay Sous-Bois",
                lastUpdated = 0L,
                latitude = 0.0,
                longitude = 0.0,
            ),
            modifier = Modifier,
        )
    }
}
