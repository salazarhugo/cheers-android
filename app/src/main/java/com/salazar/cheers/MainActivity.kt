package com.salazar.cheers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.databinding.ContentMainBinding
import com.salazar.cheers.internal.ClearRippleTheme
import com.salazar.cheers.internal.Fragment
import com.salazar.cheers.ui.detail.PostDetailViewModel
import com.salazar.cheers.ui.home.PostBottomSheet
import com.salazar.cheers.ui.otherprofile.OtherProfileViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun postDetailViewModelFactory(): PostDetailViewModel.PostDetailViewModelFactory
        fun otherProfileViewModelFactory(): OtherProfileViewModel.OtherProfileViewModelFactory
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CheersApp()

//            val systemUiController = rememberSystemUiController()
//            val useDarkIcons = !isSystemInDarkTheme()
//            val color = if (isSystemInDarkTheme()) Color.Black else Color.White
//            SideEffect {
//                systemUiController.setSystemBarsColor(color, darkIcons = useDarkIcons)
//            }

//            CheersTheme {
//                Surface(
//                    color = MaterialTheme.colorScheme.background,
//                    modifier = Modifier.systemBarsPadding(),
//                ) {
//                    MainActivityScreen()
//                }
//            }
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setupDynamicLinks()
    }

    private fun setupDynamicLinks() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null)
                    deepLink = pendingDynamicLinkData.link

                // TODO(change deep ling url to salazar-ci.com/add/username)
                if (deepLink != null) {
                    findNavController().navigate(deepLink)
                }

            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "Main Activity",
                    "getDynamicLink:onFailure",
                    e
                )
            }
    }

    @Composable
    fun MainActivityScreen() {
//        val state = rememberScaffoldState()
        val bottomSheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

        Scaffold(
            bottomBar = { BottomBar(bottomSheetState) },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                AndroidViewBinding(ContentMainBinding::inflate)
            }
        }
//        ChangeProfileBottomSheet(bottomSheetState)
        PostBottomSheet(
            sheetState = mainViewModel.sheetState,
            onDelete = {
                mainViewModel.deletePost()
            },
        ) {
        }
    }

    @Composable
    fun BottomBar(bottomSheetState: ModalBottomSheetState) {

        val user = mainViewModel.user2.value
        val coroutineScope = rememberCoroutineScope()

        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf(
            Fragment(
                R.id.homeFragment,
                { Icon(Icons.Outlined.Home, null, tint = MaterialTheme.colorScheme.onBackground) },
                { Icon(Icons.Rounded.Home, null, tint = MaterialTheme.colorScheme.onBackground) },
                "Home"
            ),
            Fragment(
                R.id.mapFragment,
                { Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.onBackground) },
                { Icon(Icons.Filled.Place, null, tint = MaterialTheme.colorScheme.onBackground) },
                "Map"
            ),
            Fragment(
                R.id.searchFragment,
                {
                    Icon(
                        painter = rememberImagePainter(R.drawable.ic_search_icon),
                        null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                {
                    Icon(
                        painter = rememberImagePainter(R.drawable.ic_search_icon_full),
                        null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                "Search"
            ),
            Fragment(
                R.id.messagesFragment,
                {
                    Icon(
                        painter = rememberImagePainter(R.drawable.ic_bubble_icon),
                        null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                {
                    Icon(
                        painter = rememberImagePainter(R.drawable.ic_bubble_icon),
                        null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                "Messages"
            ),
        )

        CompositionLocalProvider(
            LocalRippleTheme provides ClearRippleTheme
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background.compositeOver(Color.White),
                modifier = Modifier.height(52.dp),
                tonalElevation = 0.dp,
            ) {
                items.forEachIndexed { index, frag ->
                    NavigationBarItem(
                        icon = {
                            val icon = if (selectedItem == index) frag.selectedIcon else frag.icon
                            val unreadMessageCount = mainViewModel.unreadMessages.value
                            if (index == 3 && unreadMessageCount > 0)
                                BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                    icon()
                                }
                            else
                                icon()
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            val options = NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setEnterAnim(R.anim.in_from_right)
                                .setExitAnim(R.anim.out_to_left)
                                .setPopEnterAnim(R.anim.in_from_left)
                                .setPopExitAnim(R.anim.out_to_right)
                                .setPopUpTo(frag.navigationId, false)
                                .build()
                            findNavController().navigate(frag.navigationId, null, options)
                        },
//                        label = {
//                            if (frag.label != null)
//                                Text(frag.label)
//                        },
                    )
                }
                NavigationBarItem(
                    modifier = Modifier.combinedClickable(
                        onClick = { toast("Click") },
                        onLongClick = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }
                    ),
                    icon = {
                        if (user != null) {
                            Image(
                                painter = rememberImagePainter(
                                    data = user.profilePictureUrl,
                                    builder = {
                                        transformations(CircleCropTransformation())
                                        error(R.drawable.default_profile_picture)
                                    },
                                ),
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape),
                                contentDescription = null,
                            )
                        }
                    },
                    selected = selectedItem == 4,
                    onClick = {
                        selectedItem = 4
                        val options = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setEnterAnim(R.anim.in_from_right)
                            .setExitAnim(R.anim.out_to_left)
                            .setPopEnterAnim(R.anim.in_from_left)
                            .setPopExitAnim(R.anim.out_to_right)
                            .setPopUpTo(R.id.profileFragment, false)
                            .build()
                        findNavController().navigate(R.id.profileFragment, null, options)
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChangeProfileBottomSheet(bottomSheetState: ModalBottomSheetState) {
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.background)
                )
                {
                    Column {
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                    }
                }
            },
            sheetState = bottomSheetState
        ) {
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

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (mMessageReceiver),
            IntentFilter("NewMessage")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mainViewModel.onNewMessage()
        }
    }
}