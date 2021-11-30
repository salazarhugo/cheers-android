package com.salazar.cheers.ui.chat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.accompanist.insets.ProvideWindowInsets
import com.salazar.cheers.R
import com.salazar.cheers.databinding.ContentChatBinding
import com.salazar.cheers.ui.theme.CheersTheme

class ChatActivity : AppCompatActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
//            CheersTheme() {
//                Surface(color = MaterialTheme.colorScheme.background) {
                    ProvideWindowInsets(consumeWindowInsets = false) {
                        CompositionLocalProvider {
                            val scaffoldState = rememberScaffoldState()

                            Scaffold(
                                scaffoldState = scaffoldState,
                            ) {
                                AndroidViewBinding(ContentChatBinding::inflate)
                            }
                        }
//                    }
//                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}