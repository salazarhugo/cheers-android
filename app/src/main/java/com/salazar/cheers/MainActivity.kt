package com.salazar.cheers

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.databinding.ContentMainBinding
import com.salazar.cheers.ui.theme.CheersTheme
import org.neo4j.driver.*

import org.neo4j.driver.summary.ResultSummary
import java.util.Map
import org.neo4j.driver.Values.parameters
import java.lang.Exception
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.*
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
         WindowCompat.setDecorFitsSystemWindows(window, false)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }

            CheersTheme() {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.systemBarsPadding()) {
                        MainActivityScreen()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun MainActivityScreen() {

        val state = rememberScaffoldState()

        Scaffold(
            bottomBar = { BottomBar() },
            scaffoldState = state,
        ) {
                innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AndroidViewBinding(ContentMainBinding::inflate)
            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun BottomBar() {

        val user = mainViewModel.user.observeAsState(User()).value

        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf(
            Fragment(R.id.homeFragment, Icons.Outlined.Home, Icons.Rounded.Home, "Home"),
            Fragment(R.id.mapFragment, Icons.Outlined.Place, Icons.Filled.Place, "Maps"),
            Fragment(R.id.searchFragment, Icons.Outlined.Search, Icons.Filled.Search, "Search"),
//            Fragment(R.id.homeFragment, Icons.Outlined.AddBox, Icons.Rounded.AddBox, "Post"),
            Fragment(R.id.messagesFragment, Icons.Outlined.ChatBubbleOutline, Icons.Filled.ChatBubble, "Messages"),
        )

        CompositionLocalProvider(
            LocalRippleTheme provides ClearRippleTheme
        ) {
            NavigationBar {
                items.forEachIndexed { index, frag ->
                    NavigationBarItem(
                        icon = {
//                            BadgedBox(badge = { Badge { Text("") } }) {
                                if(selectedItem == index)
                                    Icon(frag.selectedIcon, contentDescription = null)
                                else
                                    Icon(frag.icon, contentDescription = null)
//                            }
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            findNavController().navigate(frag.navigationId)
                        },
                        label = {
                            if (frag.label != null)
                                Text(frag.label)
                        }
                    )
                }
                NavigationBarItem(
                    icon = {
                        BadgedBox(badge = { Badge { Text("") } }) {
                            Image(
                                painter = rememberImagePainter(
                                    data = user.photoUrl,
                                    builder = {
                                        transformations(CircleCropTransformation())
                                    }
                                ),
                                contentDescription = null,
                            )
                        }
                    },
                    selected = selectedItem == 4,
                    onClick = {
                        selectedItem = 4
                        findNavController().navigate(R.id.profileFragment)
                    }
                )
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