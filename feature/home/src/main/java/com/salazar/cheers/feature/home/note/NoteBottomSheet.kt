package com.salazar.cheers.feature.home.note

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserID


@Composable
fun NoteBottomSheet(
    userID: UserID,
    onDismiss: () -> Unit,
    navigateToCreateNote: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        NoteRoute(
            userID = userID,
            navigateBack = onDismiss,
            navigateToCreateNote = navigateToCreateNote,
        )
    }
}
