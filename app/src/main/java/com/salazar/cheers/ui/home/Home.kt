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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.salazar.cheers.R
import com.salazar.cheers.components.AnimateVisibilityFade
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

        ErrorDialog(uiState.errorMessages)

        val showDivider =
            if (uiState is HomeUiState.HasPosts)
                 uiState.listState.firstVisibleItemIndex > 0
            else
                false

        PostBottomSheet(
            sheetState = uiState.postSheetState,
            onSettingsClick = {}
        ) {
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
//                    val uploadWorkState = postViewModel.uploadWorkerState?.collectAsState(null)?.value
//                    Text(uploadWorkState?.progress.toString())
                    if (uiState.isLoading)
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    when (uiState) {
                        is HomeUiState.HasPosts -> PostList(uiState = uiState)
                        is HomeUiState.NoPosts -> NoPosts(uiState = uiState)
                    }
                    val suggestions = uiState.suggestions
                    if (suggestions != null)
                        Suggestions(suggestions = suggestions)
                }
            }
        }
    }

    @Composable
    fun NoPosts(uiState: HomeUiState.NoPosts) {
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
//            Spacer(Modifier.height(24.dp))
//            ConnectContacts()
        }
        if (uiState.suggestions != null)
            Suggestions(uiState.suggestions)
    }

    @Composable
    fun Suggestions(suggestions: List<User>) {
        val pagerState = rememberPagerState()

        HorizontalPager(
            count = suggestions.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 76.dp),
            itemSpacing = 22.dp
        ) { page ->
            Suggestion(suggestions[page])
        }

//        LazyColumn {
//            items(suggestions) { user ->
//                Suggestion(user)
//            }
//        }
    }

    @Composable
    fun Suggestion(user: User) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val photo = remember { mutableStateOf<Uri?>(null) }

                if (user.profilePicturePath.isNotBlank())
                    StorageUtil.pathToReference(user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }

                Image(
                    painter = rememberImagePainter(data = photo.value),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row() {
                    repeat(3) {
                        Image(
                            painter = painterResource(id = R.drawable.cheers),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .padding(1.dp)
                                .weight(1f)
                                .aspectRatio(1.0f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Text(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.padding(16.dp)

                ) {
                    Text("Follow")
                }
            }
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
    fun PostList(uiState: HomeUiState.HasPosts) {
        LazyColumn(state = uiState.listState) {
            uiState.posts.forEachIndexed { index, post ->
                item {
                    val isPostVisible = uiState.listState.firstVisibleItemIndex == index
//                    if (index == 0)
//                        Animate(post, isPostVisible)
//                    else
                    Post(post, isPostVisible)
                }
            }
        }
    }

    @Composable
    fun Animate(uiState: HomeUiState.HasPosts, index: Int) {
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
//            Post(uiState = uiState, index)
        }
    }

    @Composable
    fun Post(post: Post, isPostVisible: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val liked = remember { mutableStateOf(post.liked) }
            PostHeader(post)
            DividerM3()
            PostBody(post, liked, isPostVisible)
            PostFooter(post, liked)
        }
    }

    @Composable
    fun PostHeader(post: Post) {
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp, 11.dp)
                .clickable {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(
                            username = post.creator.username
                        )
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

                if (post.creator.profilePicturePath.isNotBlank())
                    StorageUtil.pathToReference(post.creator.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(
                        data = photo.value ?: R.drawable.default_profile_picture,
                        builder = {
                            transformations(CircleCropTransformation())
                            error(R.drawable.red_marker)
                        },
                    ),
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
                    Username(
                        username = post.creator.username,
                        verified = post.creator.verified,
                        textStyle = Typography.bodyMedium
                    )
                    if (post.locationName.isNotBlank())
                        Text(text = post.locationName, style = Typography.labelSmall)
                }
            }
            Icon(Icons.Default.MoreVert, "", modifier = Modifier.clickable {
                scope.launch {
                    viewModel.uiState.value.postSheetState.show()
                }
            })
        }
    }

    @Composable
    fun PostBody(
        post: Post,
        liked: MutableState<Boolean>,
        isPostVisible: Boolean,
    ) {
        val photo = remember { mutableStateOf<Uri?>(null) }
        val video = remember { mutableStateOf<Uri?>(null) }

        if (post.photoPath.isNotBlank())
            StorageUtil.pathToReference(post.photoPath)?.downloadUrl?.addOnSuccessListener {
                photo.value = it
            }

        if (post.videoPath.isNotBlank())
            StorageUtil.pathToReference(post.videoPath)?.downloadUrl?.addOnSuccessListener {
                video.value = it
            }

        Box {
            if (video.value != null)
                VideoPlayer(
                    uri = video.value!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4 / 5f)
                )
            else
                PrettyImage(
                    data = photo.value,
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
            if (post.tagUsers.isNotEmpty())
                InThisPhotoAnnotation(modifier = Modifier.align(Alignment.BottomStart))
        }
    }

    @Composable
    fun InThisPhotoAnnotation(modifier: Modifier) {
        AnimateVisibilityFade(modifier = modifier) {
            Surface(
                modifier = Modifier
                    .padding(14.dp)
                    .clickable {},
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
            ) {
                Icon(
                    Icons.Filled.AccountCircle,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(15.dp),
                    contentDescription = null
                )
            }
        }
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

                Icon(
                    icon,
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
            LikedBy(post = post)
            if (post.tagUsers.isNotEmpty())
                TagUsers(post.tagUsers)
            if (post.caption.isNotBlank())
                Caption(post)

            Spacer(Modifier.height(4.dp))
            Text(post.createdTime, style = Typography.labelSmall)
        }
        Spacer(Modifier.height(12.dp))
    }

    @Composable
    fun LikedBy(post: Post) {
        Text(
            "${post.likes} ${if (post.likes > 1) "likes" else "like"}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    fun Caption(post: Post) {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(post.creator.username)
                }
                append(" ")
                append(post.caption)
            },
            style = Typography.bodyMedium
        )
    }

    @Composable
    fun TagUsers(tagUsers: List<User>) {
        Row() {
            val n = Math.min(tagUsers.size, 3)
            repeat(n) { i ->
                val u = tagUsers[i]
                val photo = remember { mutableStateOf<Uri?>(null) }
                if (u.profilePicturePath.isNotBlank())
                    StorageUtil.pathToReference(u.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(
                        data = photo.value,
                        builder = {
                            transformations(CircleCropTransformation())
                            crossfade(true)
                            placeholder(R.drawable.default_profile_picture)
                        }
                    ),
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = -(8 * i).dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentDescription = null,
                )
            }
            Text(
                buildAnnotatedString {
                    append("With ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(tagUsers.joinToString(", ") { it.username })
                    }
                },
                style = Typography.bodyMedium
            )
        }
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

    @Composable
    fun VideoPlayer(
        uri: Uri,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current

        val player = remember(context) { ExoPlayer.Builder(context).build() }
        val playerView = remember { PlayerView(context) }

        DisposableEffect(player) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> player.play()
                    Lifecycle.Event.ON_STOP -> player.pause()
                    else -> {}
                }
            }

            viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
            onDispose {
                viewLifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                player.release()
            }
        }

        LaunchedEffect(player) {
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            playerView.player = player
            player.prepare()
            player.playWhenReady = true
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        }
        AndroidView(factory = { playerView }, modifier = modifier.clickable {
            if (player.volume == 0f) player.volume = 1f else player.volume = 0f
        }) {
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        }
    }
}
