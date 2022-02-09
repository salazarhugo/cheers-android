package com.salazar.cheers.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import com.salazar.cheers.R
import com.salazar.cheers.components.LikeButton
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.post.PostHeader
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState.HasPost,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onHeaderClicked: (username: String) -> Unit,
    onDelete: () -> Unit,
) {
    val post = uiState.postFeed.post
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed) }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            if (post.locationName.isNotBlank())
                StaticMap(
                    longitude = post.locationLongitude,
                    latitude = post.locationLatitude
                )
            Post(
                postFeed = uiState.postFeed,
                onHeaderClicked = onHeaderClicked,
                onDelete = onDelete,
                isAuthor = post.authorId == FirebaseAuth.getInstance().currentUser?.uid!!,
            )
            PrivacyText(post.privacy)
        }
    }
}

@Composable
fun PrivacyText(
    privacy: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = privacy,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun StaticMap(
    longitude: Double,
    latitude: Double,
) {
    val configuration = LocalConfiguration.current
    val token = stringResource(R.string.mapbox_access_token)
    val staticImage = remember {
        MapboxStaticMap.builder()
            .accessToken(token)
            .styleId(StaticMapCriteria.LIGHT_STYLE)
            .cameraPoint(Point.fromLngLat(longitude, latitude)) // Image's center point on map
            .staticMarkerAnnotations(
                listOf(
                    StaticMarkerAnnotation.builder().lnglat(Point.fromLngLat(longitude, latitude))
                        .build()
                )
            )
            .cameraZoom(13.0)
            .width(640) // Image width
            .height(640) // Image height
            .retina(true) // Retina 2x image will be returned
            .build()
    }

    val url = remember { staticImage.url().toString() }
    PrettyImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        data = url,
    )
}

@Composable
fun Post(
    postFeed: PostFeed,
    onHeaderClicked: (username: String) -> Unit,
    onDelete: () -> Unit,
    isAuthor: Boolean,
) {
    val post = postFeed.post
    val author = postFeed.author
    Column {
        PostHeader(
            username = author.username,
            verified = author.verified,
            locationName = post.locationName,
            onHeaderClicked = onHeaderClicked,
        )
        PostBody(post = post)
        PostFooter(post = post, onDelete = onDelete, isAuthor = isAuthor)
    }
}

@Composable
fun PostFooter(
    post: Post,
    isAuthor: Boolean,
    onDelete: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                like = post.liked,
                likes = post.likes,
                onToggle = {})
            Icon(painter = rememberImagePainter(R.drawable.ic_bubble_icon), "")
            Icon(Icons.Outlined.Share, null)
        }
        if (isAuthor)
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.clickable { onDelete() },
                tint = MaterialTheme.colorScheme.error,
            )
    }
}

@Composable
fun Toolbar(
    onBackPressed: () -> Unit,
) {
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
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Outlined.ArrowBack, "")
                }
            },
        )
    }
}


@Composable
fun PostBody(
    post: Post,
) {
    when {
        post.videoUrl.isNotBlank() -> VideoPlayer(
            uri = post.videoUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 5f)
        )
        post.photoUrl.isNotBlank() -> PrettyImage(
            data = post.photoUrl,
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
        )
        else -> Text(
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
