package com.salazar.cheers.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mapbox.maps.extension.style.layers.generated.backgroundLayer
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import kotlinx.coroutines.delay

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updatePosts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var refreshing by remember { mutableStateOf(false) }
                LaunchedEffect(refreshing) {
                    if (refreshing) {
                        delay(2000)
                        refreshing = false
                    }
                }
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = refreshing),
                    onRefresh = {
                        viewModel.updatePosts()
                        refreshing = true
                    }
                ) {
                    HomeScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen() {
        Scaffold(
            topBar = {
                Column {
                    MyAppBar()
                    DividerM3()
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { findNavController().navigate(R.id.addDialogFragment) }) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        ) {
            DividerM3()
            val posts = viewModel.posts.value
            PostList(posts = posts)
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PostList(posts: List<Post>) {
        LazyColumn() {

            posts.forEach { post ->
                stickyHeader {
                    PostHeader(post = post)
                    DividerM3()
                }
                item {
                    val state = remember {
                        MutableTransitionState(false).apply {
                            targetState = true
                        }
                    }
                    val density = LocalDensity.current
                    AnimatedVisibility(
                        visibleState = state,
                        enter = slideInVertically(
                            initialOffsetY = { with(density) { -400.dp.roundToPx() } }
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        Post(post)
                    }
                }
            }
        }
    }

    @Composable
    fun Post(post: Post) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val liked = remember { mutableStateOf(false) }
//            PostHeader(post)
//            Divider()
            PostBody(post, liked)
            PostFooter(post, liked)
        }
    }

    @Composable
    fun PostHeader(post: Post) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                .padding(14.dp, 11.dp)
                .clickable {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(post.userId)
                    findNavController().navigate(action)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Log.d("dw", post.userPhotoUrl)

                val brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD41668),
                        Color(0xFFF9B85D),
                    )
                )

                Image(
                    painter = rememberImagePainter(data = post.userPhotoUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .border(1.2.dp, brush, CircleShape)
                        .size(33.dp)
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(8.dp))
                Text(text = post.username, style = Typography.bodyMedium)
            }
            Icon(Icons.Default.MoreVert, "", modifier = Modifier.clickable {
                viewModel.deletePost(post.id)
            })
        }
    }

    @Composable
    fun PostBody(post: Post, liked: MutableState<Boolean>) {
        val photo = remember { mutableStateOf<Uri?>(null) }

        if (post.photoPath.isNotBlank())
            StorageUtil.pathToReference(post.photoPath).downloadUrl.addOnSuccessListener {
                photo.value = it
            }

        Image(
            painter = rememberImagePainter(data = photo.value),
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            liked.value = !liked.value
                            viewModel.likePost(post.id)
                        },
                    )
                }
        )
    }

    @Composable
    fun PostFooterButtons(post: Post, liked: MutableState<Boolean>) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                val icon =
                    if (liked.value) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder

                Icon(icon, "", modifier = Modifier.clickable {
                    viewModel.likePost(post.id)
                    liked.value = !liked.value
                })

                Icon(Icons.Outlined.ChatBubbleOutline, "")
                Icon(Icons.Outlined.Share, "")
            }
            Icon(Icons.Outlined.BookmarkBorder, "")
        }
    }

    @Composable
    fun PostFooter(post: Post, liked: MutableState<Boolean>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            PostFooterButtons(post, liked)
            Text(
                "${post.likes} ${if (post.likes > 1) "likes" else "like"}",
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(post.username)
                    }
                    append(" ")
                    append(post.caption)
                },
                style = Typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text("${post.createdTime} minutes ago", style = Typography.labelSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                FilledTonalButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = { /*TODO*/ }
//                ) {
//                    Text(text = "Like")
//                }
//                Spacer(Modifier.width(12.dp))
//                FilledTonalButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = { /*TODO*/ }) {
//                    Text(text = "Commment")
//                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    @Composable
    fun MyAppBar() {
        TopAppBar(
            modifier = Modifier.height(55.dp),
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 0.dp,
            title = {
                if (isSystemInDarkTheme())
                    Image(
                        painter = painterResource(R.drawable.cheers),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp),
                        contentDescription = "",
                    )
                else
                    Text("Cheers")
            },
            actions = {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
    }

}
