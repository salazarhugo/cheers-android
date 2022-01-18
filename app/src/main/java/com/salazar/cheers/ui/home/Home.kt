package com.salazar.cheers.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import androidx.work.WorkManager
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.internal.*
import com.salazar.cheers.ui.add.AddPostViewModel
import com.salazar.cheers.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.image
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by activityViewModels()
    private val el: AddPostViewModel by activityViewModels()
    lateinit var adLoader: AdLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initNativeAdd()

        return ComposeView(requireContext()).apply {
            setContent {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = false),
                    onRefresh = {
                        viewModel.refreshSuggestions()
                        viewModel.refreshPostsFlow()
                        viewModel.refreshEventsFlow()
                    },
                ) {
                    HomeScreen()
                }
            }
        }
    }

    private fun initNativeAdd() {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("2C6292E9B3EBC9CF72C85D55627B6D2D")).build()
        MobileAds.setRequestConfiguration(configuration)
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-7182026441345500/3409583237")
            .forNativeAd { ad: NativeAd ->
                viewModel.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    @Composable
    fun NativeAdPost(ad: NativeAd) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (ad.icon != null) {
                    Image(
                        rememberImagePainter(data = ad.icon.uri),
                        null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (ad.headline != null)
                    Text(ad.headline, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Text(
                        text = "Sponsored",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            DividerM3()
            AndroidView(
                factory = {
                    val adView = NativeAdView(it)
                    adView.setNativeAd(ad)
                    adView.addView(TextView(it).apply {
                        text = ad.headline
                    })

                    ad.images.forEach {
                        adView.addView(ImageView(requireContext()).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            this.image = it.drawable
                        })
                    }

                    if (ad.mediaContent.hasVideoContent()) {
                        val mediaView = MediaView(requireContext()).apply {
                            setMediaContent(ad.mediaContent!!)
                            setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        }
                        adView.addView(mediaView)
                        adView.mediaView = mediaView
                    }
                    adView
                },
                modifier = Modifier.clickable {
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }

    @Composable
    fun HomeScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        ErrorDialog(uiState.errorMessages)

        val showDivider =
            if (uiState is HomeUiState.HasPosts)
                uiState.listState.firstVisibleItemIndex > 0
            else
                false

        var toState by remember { mutableStateOf(MultiFabState.COLLAPSED) }
        Scaffold(
            topBar = {
                Column {
                    MyAppBar(uiState)
                    TopTabs(uiState = uiState)
                    if (showDivider)
                        DividerM3()
                }
            },
            floatingActionButton = {
                MultiFloatingActionButton(
                    Icons.Default.Add,
                    listOf(
                        MultiFabItem(
                            "event",
                            Icons.Outlined.Event,
                            "Event",
                        ),
                        MultiFabItem(
                            "post",
                            Icons.Outlined.PostAdd,
                            "Post",
                        )
                    ), toState, true, { state ->
                        toState = state
                    },
                    onFabItemClicked = {
                        toState = MultiFabState.COLLAPSED
                        if (it.identifier == "event")
                            findNavController().navigate(R.id.addEventFragment)
                        else
                            findNavController().navigate(R.id.addDialogFragment)
                    }
                )
            }
        ) {
            Column {
                val id = el.id.value
                if (id != null) {
                    val workInfo =
                        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(id)
                            .observeAsState().value
                    val progress = workInfo?.progress
                    val value = progress?.getDouble("Progress", 0.0)
                    val isFinish = workInfo?.state?.isFinished
                    if ((isFinish == null || !isFinish) && value != null)
                        UploadIndicator(value)
                }
                if (uiState.isLoading)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                when (uiState) {
                    is HomeUiState.HasPosts -> {
                        if (uiState.postsFlow.collectAsLazyPagingItems().itemSnapshotList.size == 0)
                            NoPosts(uiState = uiState)
                        else
                            PostList(uiState = uiState)
                    }
                }
            }
            val alpha = if (toState == MultiFabState.EXPANDED) 0.9f else 0f
            Box(
                modifier = Modifier
                    .alpha(animateFloatAsState(alpha).value)
                    .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                    .fillMaxSize()
            )
        }
    }

    @Composable
    fun TopTabs(uiState: HomeUiState) {
        val tabs = listOf("Posts", "Parties")
        val selectedTab = uiState.selectedTab
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            tabs.forEachIndexed { index, s ->
                if (index == selectedTab)
                    FilledTonalButton(onClick = { viewModel.selectTab(index) }) {
                        Text(s)
                    }
                else
                    TextButton(onClick = { viewModel.selectTab(index) }) {
                        Text(s)
                    }
            }
        }
    }

    private @Composable
    fun UploadIndicator(progress: Double) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            LinearProgressIndicator(progress = progress.toFloat() / 100f)
        }
        DividerM3()
    }

    @Composable
    fun NoPosts(uiState: HomeUiState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(22.dp),
        ) {
            val suggestions = uiState.suggestions
//            if (suggestions != null)
//                Suggestions(suggestions = suggestions)
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
    }

    @Composable
    fun Suggestions(suggestions: List<SuggestionUser>) {
        val pagerState = rememberPagerState()

        HorizontalPager(
            count = suggestions.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 76.dp),
        ) { page ->
            Suggestion(suggestions[page], this, page)
        }

    }

    @Composable
    fun Suggestion(suggestedUser: SuggestionUser, scope: PagerScope, page: Int) {
        scope.apply {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(
                                username = suggestedUser.user.username
                            )
                        findNavController().navigate(action)
                    }
                ) {

                    Image(
                        painter = rememberImagePainter(
                            data = suggestedUser.user.profilePictureUrl,
                            builder = {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            },
                        ),
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = suggestedUser.user.username,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = suggestedUser.user.fullName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(
                        modifier = Modifier.padding(0.dp, 12.dp)
                    ) {
                        repeat(3) {
                            Image(
                                painter = rememberImagePainter(suggestedUser.posts.getOrNull(it)?.photoUrl),
                                contentDescription = "Profile image",
                                modifier = Modifier
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

    @Composable
    fun PostList(uiState: HomeUiState.HasPosts) {
        val posts = uiState.postsFlow.collectAsLazyPagingItems()
        val events = uiState.eventsFlow.collectAsLazyPagingItems()

        LazyColumn(state = uiState.listState) {
            if (uiState.selectedTab == 1)
                items(events) { event ->
                    Event(event!!)
                }
            else {
                itemsIndexed(posts) { i, post ->
                    if (i != 0 && i % 4 == 0 && uiState.nativeAd != null) {
                        DividerM3()
                        NativeAdPost(ad = uiState.nativeAd)
                    }
                    when (post?.type) {
                        PostType.TEXT -> Post(post, true)
                        PostType.IMAGE -> Post(post, true)
                        PostType.VIDEO -> Post(post, true)
                    }
                }
            }

            posts.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            CircularProgressIndicatorM3(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            CircularProgressIndicatorM3(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = posts.loadState.append as LoadState.Error
                        item {
                            Text(
                                text = e.error.localizedMessage!!,
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    }
                }
            }
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
//            if (post.type != PostType.TEXT)
//                DividerM3()
            PostBody(post, liked, isPostVisible)
            PostFooter(post)
        }
    }

    @Composable
    fun PostHeader(post: Post) {
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
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

                Image(
                    painter = rememberImagePainter(
                        data = post.creator.profilePictureUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        },
                    ),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .border(1.2.dp, brush, CircleShape)
                        .size(36.dp)
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
                    if (post.locationName.isBlank())
                        Text(
                            post.createdTime,
                            style = Typography.labelMedium
                        )
                    else
                        Text(text = post.locationName, style = Typography.labelSmall)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                Box(
//                    modifier = Modifier
//                        .padding(end = 4.dp)
//                        .size(4.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.onBackground)
//                )
                if (post.locationName.isNotBlank())
                    Text(
                        post.createdTime,
                        style = Typography.labelMedium
                    )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.selectPost(post.id)
                        mainViewModel.selectPost(post.id)
                        scope.launch {
                            mainViewModel.sheetState.show()
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.MoreHoriz, null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    @Composable
    fun PostBody(
        post: Post,
        liked: MutableState<Boolean>,
        isPostVisible: Boolean,
    ) {
        Box {
            if (post.videoUrl.isNotBlank())
                VideoPlayer(
                    uri = post.videoUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4 / 5f)
                )
            else if (post.photoUrl.isNotBlank())
                PrettyImage(
                    data = post.photoUrl,
                    contentDescription = "avatar",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)// or 4/5f
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    liked.value = !liked.value
                                    viewModel.toggleLike(post)
                                },
                            )
                        }
                        .clickable {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToPostDetailFragment(
                                    postId = post.id
                                )
                            findNavController().navigate(action)
                        }
                )
            else
                Text(
                    text = post.caption,
                    modifier = Modifier.padding(16.dp)
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
                    .padding(32.dp)
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
    fun PostFooterButtons(post: Post) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LikeButton(
                    like = post.liked,
                    likes = post.likes,
                    onToggle = { viewModel.toggleLike(post) })
                Icon(painter = rememberImagePainter(R.drawable.ic_bubble_icon), "")
                Icon(Icons.Outlined.Share, null)
            }
            Icon(Icons.Outlined.BookmarkBorder, null)
        }
    }

    @Composable
    fun PostFooter(post: Post) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            PostFooterButtons(post)
            if (post.type != PostType.TEXT) {
                LikedBy(post = post)
                if (post.tagUsers.isNotEmpty())
                    TagUsers(post.tagUsers)
                if (post.caption.isNotBlank())
                    Caption(post)
            }
        }
        if (post.type != PostType.TEXT)
            Spacer(Modifier.height(12.dp))
    }

    @Composable
    fun LikedBy(post: Post) {
        Text(
            "${post.likes} ${if (post.likes > 1) "likes" else "like"}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                val action = HomeFragmentDirections.actionHomeFragmentToLikesFragment(
                    postId = post.id
                )
                findNavController().navigate(action)
            }
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
        Row {
            val n = Math.min(tagUsers.size, 3)
            repeat(n) { i ->
                val u = tagUsers[i]
                Image(
                    painter = rememberImagePainter(
                        data = u.profilePictureUrl,
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
    fun MyAppBar(uiState: HomeUiState) {
        val icon =
            if (isSystemInDarkTheme()) R.drawable.ic_cheers_logo else R.drawable.ic_cheers_logo
        SmallTopAppBar(
//        CenterAlignedTopAppBar(
//            modifier = Modifier.height(50.dp),
            title = {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = "",
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
                IconButton(onClick = {
                    findNavController().navigate(R.id.cameraFragment)
                }) {
                    Icon(
                        Icons.Outlined.Camera,
                        contentDescription = "Activity icon"
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
        uri: String,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current

        // Create media item
        val mediaItem = MediaItem.fromUri(uri)

        // Create the player
        val player = remember {
            ExoPlayer.Builder(context).build().apply {
                this.setMediaItem(mediaItem)
                this.prepare()
                this.playWhenReady = true
                this.repeatMode = Player.REPEAT_MODE_ALL
                this.volume = 0f
                this.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        }

        DisposableEffect(
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        this.player = player
                    }
                },
                modifier = modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (player.volume == 0f) player.volume = 1f else player.volume = 0f
                    }
            ) {
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
//                it.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            }
        ) {
            onDispose {
                player.release()
            }
        }
    }

    @Composable
    fun Event(post: EventUi) {
        val event = post.event
        Column(
            modifier = Modifier.clickable {
                val action = HomeFragmentDirections.actionHomeFragmentToEventDetailFragment(
                    eventId = post.event.id
                )
                findNavController().navigate(action)
            }
        ) {
            Image(
                painter = rememberImagePainter(
                    data = event.imageUrl,
                    builder = {
                        error(R.drawable.image_placeholder)
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = post.host.profilePictureUrl,
                            builder = {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            },
                        ),
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(16.dp))
                    Username(
                        username = post.host.username,
                        verified = post.host.verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                }

                val d = remember { ZonedDateTime.parse(event.startDate) }
                Text(
                    d.toLocalDateTime().format(DateTimeFormatter.ofPattern("E, d MMM hh:mm a")),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (event.name.isNotBlank())
                    Text(event.name, style = MaterialTheme.typography.titleLarge)
                if (event.description.isNotBlank())
                    Text(
                        event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                if (event.locationName.isNotBlank())
                    Text(text = event.locationName, style = Typography.labelSmall)
                Text("4.8k interested - 567 going", modifier = Modifier.padding(vertical = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilledTonalButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Star, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Interested")
                    }
                    FilledTonalButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Going")
                    }
                }
            }
        }
    }
}
