package com.salazar.cheers.core.ui.components.post

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.PostCaption
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.playback.PlaybackComponent
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.ui.components.post.more.PostMoreBottomSheet
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun PostComponent(
    post: Post,
    modifier: Modifier = Modifier,
    audioState: AudioState? = null,
    onAudioClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onLikeClick: () -> Unit = {},
    onLikeCountClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    navigateToDeleteDialog: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val drinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { post.photos.size },
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PostHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
            username = post.username,
            verified = post.verified,
            avatar = post.profilePictureUrl,
            locationName = post.locationName,
            createTime = post.createTime,
            isPublic = post.privacy == Privacy.PUBLIC.name,
            onUserClick = { onUserClick(post.authorId) },
            onMoreClick = {
                showBottomSheet = true
            },
        )
        PostCaption(
            caption = post.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onUserClicked = onUserClick,
            onPostClicked = {},
        )
        PostMedia(
            medias = post.photos.map { Media.Image(uri = Uri.parse(it)) },
            pagerState = pagerState,
            modifier = Modifier,
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
                    .padding(horizontal = 16.dp),
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
                .padding(horizontal = 16.dp),
            likeCount = post.likes,
            commentCount = post.comments,
            hasViewerLiked = post.liked,
            onLikeClick = onLikeClick,
            onLikeCountClick = onLikeCountClick,
            onCommentClick = onCommentClick,
            onShareClick = { },
        )
    }

    if (showBottomSheet) {
        PostMoreBottomSheet(
            isAuthor = post.isAuthor,
            sheetState = drinkSheetState,
            onDismissRequest = {
                scope.launch {
                    drinkSheetState.hide()
                }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            navigateToPostDetails = onDetailsClick,
            navigateToDeleteDialog = navigateToDeleteDialog,
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
//            modifier = Modifier.padding(16.dp),
            post = Post(
                username = "cheers",
                isAuthor = true,
                drinkName = "Beer",
                caption = "\uD83D\uDCE2 Invest in a warehouse with showroom in Agios Nicolaos, Larnaka!",
                createTime = Date().time / 1000,
                likes = 346334,
                comments = 325,
                photos = listOf("", "", "", "", ""),
                audioWaveform = listOf(4, 5, 3, 7, 2, 3, 5, 2, 5, 3, 5, 6, 7, 8, 9),
                drinkPicture = "wf",
                drinkId = "",
            ),
        )
    }
}
