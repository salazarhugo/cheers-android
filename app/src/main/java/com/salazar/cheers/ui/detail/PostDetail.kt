package com.salazar.cheers.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.home.HomeFragmentDirections
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private val args: PostDetailFragmentArgs by navArgs()

    @Inject
    lateinit var postDetailViewModelFactory: PostDetailViewModel.PostDetailViewModelFactory

    private val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModel.provideFactory(postDetailViewModelFactory, postId = args.postId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PostDetailScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PostDetailScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        Scaffold(
            topBar = { Toolbar() }
        ) {
            when (uiState) {
                is PostDetailUiState.HasPost -> Post(post = uiState.post)
                is PostDetailUiState.NoPosts -> {
                    Text("No post")
                }
            }
        }
    }

    @Composable
    fun Post(post: Post) {
        Column {
            PostHeader(post = post)
            PostBody(post = post)
        }
    }

    @Composable
    fun Toolbar() {
        Column {
            SmallTopAppBar(
                title = {
                    Text(
                        "Detail",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        findNavController().popBackStack()
                    }) {
                        Icon(Icons.Outlined.ArrowBack, "")
                    }
                },
            )
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
                        PostDetailFragmentDirections.actionPostDetailFragmentToOtherProfileFragment(
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
            })
        }
    }

    @Composable
    fun PostBody(
        post: Post,
    ) {
        Box(
        ) {
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
                        .fillMaxWidth()
                )
            else
                Text(
                    text = post.caption,
                    modifier = Modifier.padding(14.dp)
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
                modifier = modifier.clickable {
                    if (player.volume == 0f) player.volume = 1f else player.volume = 0f
                }
            ) {
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        ) {
            onDispose {
                player.release()
            }
        }
    }
}
