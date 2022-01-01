package com.salazar.cheers.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowCompat
import androidx.navigation.navArgs
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.toast


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChatActivityArgs by navArgs()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val channelId = args.channelId
        chatViewModel.channelId.value = channelId
        chatViewModel.seenLastMessage(channelId)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }

            ProvideWindowInsets(consumeWindowInsets = false, windowInsetsAnimationsEnabled = true) {
                CompositionLocalProvider {
                    CheersTheme {
                        val messages = chatViewModel.messages(channelId)
                            .collectAsState(initial = listOf()).value
                        val localClipboardManager = LocalClipboardManager.current
                        ChatScreen(
                            channelId = channelId,
                            name = args.name,
                            username = args.username,
                            profilePicturePath = args.profilePicturePath,
                            messages = messages,
                            onMessageSent = ::senMessage,
                            onUnsendMessage = {
                                chatViewModel.unsendMessage(
                                    channelId = channelId,
                                    messageId = it.id
                                )
                            },
                            onDoubleTapMessage = {
                                chatViewModel.likeMessage(
                                    channelId = channelId,
                                    messageId = it.id
                                )
                            },
                            onUnlike = {
                                chatViewModel.unlikeMessage(
                                    channelId = channelId,
                                    messageId = it.id
                                )
                            },
                            onCopyText = {
                                localClipboardManager.setText(AnnotatedString(it))
                                toast("Copied text to clipboard")
                            },
                            onImageSelectorClick = {
                                Utils.openPhotoChooser(multipleImageResultLauncher, false) // TODO (Allow multiple images)
                            },
                            onPoBackStack = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    private val multipleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val clipData = data?.clipData
                if (clipData != null) {
                    val count: Int = clipData.itemCount
                    val imagesUri = mutableListOf<Uri>()
                    for (i in 0 until count) {
                        val imageUri: Uri = clipData.getItemAt(i).uri
                        imagesUri.add(imageUri)
                    }
                    chatViewModel.sendImageMessage(images = imagesUri, channelId = args.channelId)
                } else if (data != null) {
                    val selectedImageUri: Uri? = data.data
                    if (selectedImageUri != null)
                        chatViewModel.sendImageMessage(
                            images = listOf(selectedImageUri),
                            channelId = args.channelId
                        )
                }
            }
        }

    private fun senMessage(channelId: String, msg: String) {
        chatViewModel.sendTextMessage(msg, channelId)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return findNavController().navigateUp() || super.onSupportNavigateUp()
//    }
//
//    private fun findNavController(): NavController {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.mobile_navigation_chat) as NavHostFragment
//        return navHostFragment.navController
//    }
}