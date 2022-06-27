package com.salazar.cheers.ui.main.chat

import android.Manifest
import android.content.ContentValues.TAG
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.utils.Permission
import com.salazar.cheers.internal.User
import com.salazar.cheers.navigation.CheersNavigationActions
import java.io.File
import java.io.IOException


/**
 * Stateful composable that displays the Navigation route for the Chat screen.
 *
 * @param chatViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                chatViewModel.sendImageMessage(listOf(it))
        }

    val micInteractionSource = remember { MutableInteractionSource() }
    val isPressed by micInteractionSource.collectIsPressedAsState()

    val mediaRecorder = remember { MediaRecorder() }
    if (isPressed) {
        Permission(permission = Manifest.permission.RECORD_AUDIO) {
            Permission(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                LaunchedEffect(Unit) {
                        mediaRecorder.startRecording()
                }
                DisposableEffect(Unit) {
                    onDispose {
                        mediaRecorder.stopRecording()
                    }
                }
            }
        }
    }


    when (uiState) {
        is ChatUiState.HasChannel -> {
            val ui = (uiState as ChatUiState.HasChannel)
            val otherUser = User()

            ChatScreen(
                uiState = uiState as ChatUiState.HasChannel,
                onTitleClick = { navActions.navigateToOtherProfile(it) },
                onPoBackStack = { navActions.navigateBack() },
                onUnlike = chatViewModel::unlikeMessage,
                onLike = chatViewModel::likeMessage,
                onUnsendMessage = chatViewModel::unsendMessage,
                onMessageSent = chatViewModel::sendTextMessage,
                onImageSelectorClick = { launcher.launch("image/*") },
                onCopyText = {},
                username = ui.channel.username,
                verified = ui.channel.verified,
                name = ui.channel.name,
                profilePicturePath = ui.channel.avatarUrl,
                onAuthorClick = { navActions.navigateToOtherProfile(it) },
                onTextChanged = chatViewModel::onTextChanged,
                onInfoClick = { navActions.navigateToRoomDetails(ui.channel.id) },
                micInteractionSource = micInteractionSource,
            )
        }
        is ChatUiState.NoChannel -> {
            LoadingScreen()
        }
    }
}

private fun MediaRecorder.startRecording() {

    var audiofile: File? = null

    val dir = Environment.getExternalStorageDirectory()
    try {
        audiofile = File.createTempFile("sound", ".3gp", dir)
    } catch (e: IOException) {
        Log.e(TAG, e.toString())
        return
    }

    setAudioSource(MediaRecorder.AudioSource.MIC)
    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
    setOutputFile(audiofile.absolutePath)

    try {
        prepare()
    } catch (e: IOException) {
            Log.e("TAG", e.toString())
    }

    start()
}

fun MediaRecorder.stopRecording() {
    stop()
    reset()
    release()
}