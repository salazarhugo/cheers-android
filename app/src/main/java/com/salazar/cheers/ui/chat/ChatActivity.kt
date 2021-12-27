package com.salazar.cheers.ui.chat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.ui.theme.CheersTheme

class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val channelId = intent.getStringExtra("chatChannelId").toString()
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
                        ChatScreen(channelId = channelId, messages = messages, ::senMessage) {
//                            Navigation.findNavController(this).popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun senMessage(channelId: String, msg: String) {
        chatViewModel.sendTextMessage(msg, channelId)
    }
}