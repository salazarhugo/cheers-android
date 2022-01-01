package com.salazar.cheers

import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.databinding.ContentMainBinding
import com.salazar.cheers.internal.ClearRippleTheme
import com.salazar.cheers.internal.Fragment
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.StorageUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import com.snapchat.kit.sdk.bitmoji.networking.FetchAvatarUrlCallback

import com.snapchat.kit.sdk.Bitmoji





@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            val color = if (isSystemInDarkTheme()) Color.Black else Color.White
            SideEffect {
                systemUiController.setSystemBarsColor(color, darkIcons = useDarkIcons)
            }

            CheersTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.systemBarsPadding(),
                ) {
                    MainActivityScreen()
                }
            }
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setupDynamicLinks()
    }

    private fun getBitmojiAvatar() {
        Bitmoji.fetchAvatarUrl(this, object : FetchAvatarUrlCallback {
            override fun onSuccess(@Nullable avatarUrl: String?) {
                if (avatarUrl != null)
                    toast(avatarUrl)
            }

            override fun onFailure(isNetworkError: Boolean, statusCode: Int) {
                toast(statusCode.toString())
            }
        })
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
            .addOnFailureListener(this) { e -> Log.w("Main Activity", "getDynamicLink:onFailure", e) }
    }

    @ExperimentalMaterial3Api
    @OptIn(ExperimentalMaterialApi::class)
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
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
            ) {
                AndroidViewBinding(ContentMainBinding::inflate)
            }
        }
        ChangeProfileBottomSheet(bottomSheetState)
    }

    @ExperimentalMaterialApi
    @OptIn(ExperimentalFoundationApi::class)
    @ExperimentalCoilApi
    @Composable
    fun BottomBar(bottomSheetState: ModalBottomSheetState) {

        val user = mainViewModel.user2.value
        val coroutineScope = rememberCoroutineScope()

        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf(
            Fragment(R.id.homeFragment, Icons.Outlined.Home, Icons.Rounded.Home, "Home"),
            Fragment(R.id.mapFragment, Icons.Outlined.Place, Icons.Filled.Place, "Maps"),
            Fragment(R.id.searchFragment, Icons.Outlined.Search, Icons.Filled.Search, "Search"),
//            Fragment(R.id.homeFragment, Icons.Outlined.AddBox, Icons.Rounded.AddBox, "Post"),
            Fragment(
                R.id.messagesFragment,
                Icons.Outlined.ChatBubbleOutline,
                Icons.Filled.ChatBubble,
                "Messages"
            ),
        )

        CompositionLocalProvider(
            LocalRippleTheme provides ClearRippleTheme
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
                modifier = Modifier.height(52.dp)
            ) {
                items.forEachIndexed { index, frag ->
                    NavigationBarItem(
                        icon = {
                            if (selectedItem == index)
                                Icon(frag.selectedIcon, contentDescription = null)
                            else
                                Icon(frag.icon, contentDescription = null)
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
                            val photo = remember { mutableStateOf<Uri?>(null) }
                            if (user.profilePicturePath.isNotBlank())
                                StorageUtil.pathToReference(user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                                    photo.value = it
                                }
                            Image(
                                painter = rememberImagePainter(
                                    data = photo.value,
                                    builder = {
                                        transformations(CircleCropTransformation())
                                        placeholder(R.drawable.default_profile_picture)
                                    }
                                ),
                                modifier = Modifier.size(30.dp),
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
}