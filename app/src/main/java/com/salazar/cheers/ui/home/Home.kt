package com.salazar.cheers.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.PullRefresh
import com.salazar.cheers.components.rememberPullRefreshState
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshPosts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = false),
                    onRefresh = {
                        viewModel.refreshPosts()
                    },
                ) {
                    HomeScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen() {
        val uiState = viewModel.uiState.collectAsState().value
        val listState = rememberLazyListState()

        ErrorDialog(uiState.errorMessages)

        val showDivider by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        Scaffold(
            topBar = {
                Column {
                    MyAppBar()
                    if (showDivider)
                        DividerM3()
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { findNavController().navigate(R.id.addDialogFragment) }) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        ) {
            Column {
                if (uiState.isLoading)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                when (uiState) {
                    is HomeUiState.HasPosts -> PostList(
                        posts = uiState.posts,
                        listState = listState
                    )
                    is HomeUiState.NoPosts -> NoPosts()
                }
            }
        }
    }

    @Composable
    fun NoPosts() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                "Welcome to Cheers",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Follow people to start seeing the photos and videos they share.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(24.dp))
            ConnectContacts()
            Suggestions()
        }
    }

    @Composable
    fun Suggestions() {
        Card()
        {

        }
    }

    @Composable
    fun ConnectContacts() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                Icons.Outlined.ContactPage,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                    .padding(12.dp)
            )
            Column {
                Text("Connect Contacts", style = MaterialTheme.typography.bodyMedium)
                Text("Follow people you know", style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Connect")
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PostList(posts: List<Post>, listState: LazyListState) {

        LazyColumn(state = listState) {
            item {
                Spacer(Modifier.height(100.dp))
            }
            posts.forEachIndexed { index, post ->
                item {
                    if (index == 0)
                        Animate(post)
                    else
                        Post(post)
                }
            }
        }
    }

    @Composable
    fun Animate(post: Post) {
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

    @Composable
    fun Post(post: Post) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val liked = remember { mutableStateOf(post.liked) }
            PostHeader(post)
            DividerM3()
            PostBody(post, liked)
            PostFooter(post, liked)
        }
    }

    @Composable
    fun PostHeader(post: Post) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
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

                val brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD41668),
                        Color(0xFFF9B85D),
                    )
                )

                val photo = remember { mutableStateOf<Uri?>(null) }

                if (post.userPhotoUrl.isNotBlank())
                    StorageUtil.pathToReference(post.userPhotoUrl)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(data = photo.value),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .border(1.2.dp, brush, CircleShape)
                        .size(33.dp)
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(text = post.username, style = Typography.bodyMedium)
                    if (post.locationName.isNotBlank())
                        Text(text = post.locationName, style = Typography.labelSmall)
                }
                if (post.verified) {
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = rememberImagePainter(R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                }
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
            StorageUtil.pathToReference(post.photoPath)?.downloadUrl?.addOnSuccessListener {
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
                            viewModel.toggleLike(post.id, liked.value)
                        },
                    )
                }
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
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

//                var count by remember { mutableStateOf(0) }
                val transition = updateTransition(
                    targetState = liked.value, label = ""
                )
//                transition.animateSize(label = "Size") { state ->
//                    when(state)                    {
//                        true -> Size(36f, 36f)
//                        false -> Size.Unspecified
//                    }
//                }

                val color by transition.animateColor(label = "Color") { state ->
                    when (state) {
                        true -> Color.Red
                        false -> MaterialTheme.colorScheme.onBackground
                    }
                }

                Icon(icon,
                    "",
                    modifier = Modifier.clickable {
                        liked.value = !liked.value
                        viewModel.toggleLike(post.id, liked.value)
                    }, color
                )

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
        }
        Spacer(Modifier.height(12.dp))
    }

    @Composable
    fun MyAppBar() {
        SmallTopAppBar(
//            modifier = Modifier.height(55.dp),
//            backgroundColor = MaterialTheme.colorScheme.surface,
//            elevation = 0.dp,
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
                    Text(
                        text = "Cheers",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                    )
            },
            actions = {
                IconButton(onClick = {
                    findNavController().navigate(R.id.activityFragment)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Activity icon"
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

    @Composable
    fun ErrorDialog(errorMessages: List<String>) {
        val openDialog = remember { mutableStateOf(false) }

        if (errorMessages.isNotEmpty())
            openDialog.value = true

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.deleteErrorMessage()
                    openDialog.value = false
                },
                title = {
                    Text(text = "An error occured")
                },
                text = {
                    errorMessages.forEach {
                        Text(text = it)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteErrorMessage()
                            openDialog.value = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteErrorMessage()
                            openDialog.value = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )

        }
    }
}
