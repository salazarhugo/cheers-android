package com.salazar.cheers.feature.map.screens.map

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.map.ui.dialogs.PostMapDialog
import com.salazar.cheers.feature.map.ui.dialogs.UserMapDialog

@Composable
fun MapBottomSheet(
    state: SheetState,
    type: MapAnnotation,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onMapUIAction: (MapUIAction) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        when (type) {
            is MapAnnotation.PostAnnotation -> {
                PostMapDialog(
                    post = type.post,
                )
            }

            is MapAnnotation.UserAnnotation -> {
                UserMapDialog(
                    userLocation = type.user,
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
    val userLocation = com.salazar.cheers.data.map.UserLocation(
        id = "",
        picture = "",
        username = "cheers",
        verified = true,
        name = "Cheers Social",
        locationName = "Aulnay Sous-Bois",
        lastUpdated = 0L,
        latitude = 0.0,
        longitude = 0.0,
    )
    CheersPreview {
        MapBottomSheet(
            state = rememberModalBottomSheetState(),
            type = MapAnnotation.UserAnnotation(userLocation),
            modifier = Modifier,
        )
    }
}
