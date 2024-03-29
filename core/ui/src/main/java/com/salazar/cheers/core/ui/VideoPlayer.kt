package com.salazar.cheers.core.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@OptIn(UnstableApi::class) @Composable
fun VideoPlayer(
    uri: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create media item
    val mediaItem = MediaItem.fromUri(uri)

    // Create the player
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    when (player.volume) {
                        0f -> player.volume = 1f
                        else -> player.volume = 0f
                    }
                },
            factory = {
                PlayerView(it).apply {
                    this.player = player
                }
            },
            update = {
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                it.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            }
        )
    ) {
        onDispose {
            player.release()
        }
    }
}


@ComponentPreviews
@Composable
private fun VideoPlayerPreview() {
    CheersPreview {
        VideoPlayer(
            uri = "",
            modifier = Modifier.padding(16.dp),
        )
    }
}

