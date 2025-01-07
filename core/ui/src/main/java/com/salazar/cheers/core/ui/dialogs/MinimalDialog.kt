package com.salazar.cheers.core.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.extensions.noRippleClickable

@Composable
fun AvatarDialog(
    avatar: String?,
    name: String?,
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { onDismissRequest() },
            contentAlignment = Alignment.Center,
        ) {
            AvatarComponent(
                avatar = avatar,
                name = name,
                size = 220.dp,
            )
        }
    }
}

@Preview
@Composable
private fun AvatarDialogPreview() {
    CheersPreview {
        AvatarDialog(
            avatar = "",
            name = cheersUserItem.name,
        )
    }
}