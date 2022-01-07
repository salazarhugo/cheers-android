package com.salazar.cheers.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen()
            }
        }
    }

    @Composable
    fun ProfileScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        ProfileBottomSheet(
            sheetState = uiState.sheetState,
            onSettingsClick = {
                val action = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> LoadingScreen()
                is ProfileUiState.HasUser -> Profile(uiState)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Profile(uiState: ProfileUiState.HasUser) {
        Scaffold(
            topBar = { Toolbar(uiState = uiState) }
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Section1(user = uiState.user)
                    Section2(user = uiState.user)
                    Spacer(Modifier.height(4.dp))
                    Row {
                        OutlinedButton(
                            onClick = { findNavController().navigate(R.id.editProfileFragment) },
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth(),
//                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Text("Edit Profile", color = MaterialTheme.colorScheme.onBackground)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Outlined.BookmarkBorder, "")
                        }
                    }
                }
                ProfilePostsAndTags()
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun ProfilePostsAndTags() {
        val posts = viewModel.posts.value
        val tabs = listOf(Icons.Default.GridView, Icons.Outlined.AssignmentInd)
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()

        TabRow(
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            },
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) {
            // Add tabs for all of our pages
            tabs.forEachIndexed { index, icon ->
                Tab(
                    icon = { Icon(icon, null) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
//                        viewModel.toggle()
                    },
                )
            }
        }
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (page) {
                    0 -> GridViewPosts(posts = posts)
                    1 -> {}
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun GridViewPosts(posts: List<Post>) {
        LazyVerticalGrid(
            cells = GridCells.Adaptive(minSize = 100.dp),
        ) {
            items(posts) { post ->
                PostItem(post)
            }
        }
    }

    @Composable
    fun PostItem(post: Post) {
        Box(
            modifier = Modifier.padding(1.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            val url = if (post.type == PostType.VIDEO) post.videoThumbnailUrl else post.photoUrl
            PrettyImage(
                data = url,
                contentDescription = "avatar",
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)// or 4/5f
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { })
                    }
            )

            if (post.type == PostType.VIDEO)
                PlayIcon()

        }
    }

    @Composable
    fun PlayIcon() {
        Icon(
            Icons.Rounded.PlayArrow,
            null,
            modifier = Modifier.padding(8.dp)
        )
    }

    @Composable
    fun Section2(user: User) {
        Column {
            Text(
                text = user.fullName,
                style = Typography.bodyMedium
            )
            Text(
                user.bio,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
            ClickableText(
                text = AnnotatedString(user.website),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Normal
                ),
                onClick = { offset ->
                },
            )
        }
    }

    @Composable
    fun Toolbar(uiState: ProfileUiState.HasUser) {
        val otherUser = uiState.user
        val scope = rememberCoroutineScope()
        Column {
            SmallTopAppBar(
//                modifier = Modifier.height(55.dp),
//                backgroundColor = MaterialTheme.colorScheme.surface,
//                elevation = 0.dp,
                title = {
                    Username(
                        username = otherUser.username,
                        verified = otherUser.verified,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Roboto
                        ),
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.AddBox, "")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            uiState.sheetState.show()
                        }
                    }) {
                        Icon(Icons.Rounded.Menu, "")
                    }
                },
            )
            DividerM3()
        }
    }

    @Composable
    fun Section1(user: User) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {

            Image(
                painter = rememberImagePainter(
                    data = user.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentDescription = null,
            )
            Counters(user)
            Spacer(Modifier.height(18.dp))
        }
    }

    @Composable
    fun Counters(user: User) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            val items = listOf(
                Counter("Posts", user.posts, null),
                Counter("Followers", user.followers, R.id.followersFollowingFragment),
                Counter("Following", user.following, R.id.followersFollowingFragment),
            )

            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (item.navId != null) {
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToFollowersFollowingFragment(
                                    user.username
                                )
                            findNavController().navigate(action)
                        }
                    }
                ) {
                    Text(
                        text = item.value.toString(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                    Text(text = item.name, fontSize = 14.sp, fontFamily = Roboto)
                }
            }
        }
    }
}
