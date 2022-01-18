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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowCompat
import androidx.navigation.navArgs
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.toast
import javax.inject.Inject


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val args: ChatActivityArgs by navArgs()

    @Inject
    lateinit var chatViewModelFactory: ChatViewModel.ChatViewModelFactory

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModel.provideFactory(chatViewModelFactory, args.channelId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val channelId = args.channelId

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
                    chatViewModel.sendImageMessage(images = imagesUri)
                } else if (data != null) {
                    val selectedImageUri: Uri? = data.data
                    if (selectedImageUri != null)
                        chatViewModel.sendImageMessage(images = listOf(selectedImageUri))
                }
            }
        }
}