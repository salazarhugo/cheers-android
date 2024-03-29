package com.salazar.cheers.core.ui.components.post

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.PostCaption
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.playback.PlaybackComponent
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.data.post.repository.Post
import java.util.Date

@Composable
fun PostComponent(
    post: Post,
    modifier: Modifier = Modifier,
    audioState: AudioState? = null,
    onAudioClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onMoreClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onLikeCountClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    val pagerState = rememberPagerState(
        pageCount = { post.photos.size },
    )

    Column(
        modifier = modifier,
    ) {
        PostHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            username = post.username,
            verified = post.verified,
            avatar = post.profilePictureUrl,
            locationName = post.locationName,
            createTime = post.createTime,
            isPublic = post.privacy == Privacy.PUBLIC.name,
            onUserClick = { onUserClick(post.authorId) },
            onMoreClick = onMoreClick,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

        }
        PostCaption(
            caption = post.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            onUserClicked = onUserClick,
            onPostClicked = {},
        )
        PostMedia(
            medias = post.photos.map { Media.Image(uri = Uri.parse(it)) },
            pagerState = pagerState,
            modifier = Modifier
                .padding(top = 8.dp),
            onPostClick = { },
            onDoubleTap = {
                if (post.liked.not()) {
                    onLikeClick()
                }
            },
        )
        if (post.audioWaveform.isNotEmpty()) {
            PlaybackComponent(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(16.dp),
                audioState = audioState,
                amplitudes = post.audioWaveform,
                onClick = onAudioClick,
            )
        }
        PostDrink(
            drink = post.drinkName,
            picture = post.drinkPicture,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        PostFooter(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            likeCount = post.likes,
            commentCount = post.comments,
            hasViewerLiked = post.liked,
            onLikeClick = onLikeClick,
            onLikeCountClick = onLikeCountClick,
            onCommentClick = onCommentClick,
            onShareClick = { },
        )
    }
}

@ComponentPreviews
@Composable
private fun PostComponentPreview() {
    CheersPreview {
        PostComponent(
            modifier = Modifier.padding(16.dp),
            post = Post(
                username = "cheers",
                isAuthor = true,
                caption = "\uD83D\uDCE2 Invest in a warehouse with showroom in Agios Nicolaos, Larnaka!",
                drinkName = "Beer",
                createTime = Date().time / 1000,
                likes = 346334,
                comments = 325,
                audioWaveform = listOf(4, 5, 3, 7, 2, 3, 5, 2, 5, 3, 5, 6, 7, 8, 9),
            ),
        )
    }
}

@ComponentPreviews
@Composable
private fun PostComponentPreviewImages() {
    CheersPreview {
        PostComponent(
            modifier = Modifier.padding(16.dp),
            post = Post(
                username = "cheers",
                isAuthor = true,
                drinkName = "Beer",
                createTime = Date().time / 1000,
                likes = 346334,
                comments = 325,
                photos = listOf("", "", "", "", ""),
            ),
        )
    }
}
