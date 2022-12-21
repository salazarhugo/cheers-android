package com.salazar.cheers.ui.compose.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

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
                StyledPlayerView(context).apply {
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

