package com.salazar.cheers.core.ui.components.post.more

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.GreySheet

@Composable
fun PostMoreBottomSheet(
    isAuthor: Boolean,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = {},
    navigateToPostDetails: () -> Unit = {},
    navigateToDeleteDialog: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (!isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surface
        } else {
            GreySheet
        },
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
    ) {
        PostMoreBottomSheetStateful(
            isAuthor = isAuthor,
            onDetailsClick = navigateToPostDetails,
            onDeleteClick = navigateToDeleteDialog,
        )
    }
}


@ComponentPreviews
@Composable
private fun PostMoreBottomSheetPreview() {
    CheersPreview {
        PostMoreBottomSheet(
            isAuthor = true,
            modifier = Modifier,
        )
    }
}