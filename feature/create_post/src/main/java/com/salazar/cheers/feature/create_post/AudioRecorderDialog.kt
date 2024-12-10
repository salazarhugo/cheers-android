package com.salazar.cheers.feature.create_post

import android.Manifest
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.theme.GreySheet
import com.salazar.cheers.core.ui.ui.Permission
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.feature.create_post.audio_recorder.AudioRecorderScreen
import com.salazar.cheers.feature.create_post.audio_recorder.AudioRecorderUIAction


@Composable
fun AudioRecorderDialog(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onDone: (LocalAudio) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        Permission(
            permission = Manifest.permission.RECORD_AUDIO,
        ) {
            AudioRecorderScreen(
                modifier = Modifier,
                onDone = {
                    onDone(it)
                    onDismiss()
                },
                onAudioRecorderUIAction = {
                    when(it) {
                        AudioRecorderUIAction.OnBackPressed -> onDismiss()
                        else -> {}
                    }
                }
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun VoiceRecordingDialogPreview() {
    CheersPreview {
        AudioRecorderDialog(
            modifier = Modifier,
        )
    }
}