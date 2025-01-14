package com.salazar.cheers.core.ui.components.post

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.PostCaption
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.multi_avatar.MultiAvatarComponent
import com.salazar.cheers.core.ui.components.playback.PlaybackComponent
import com.salazar.cheers.core.ui.components.post.footer.PostFooter
import com.salazar.cheers.core.ui.components.post.mentions.MentionsBottomSheet
import com.salazar.cheers.core.ui.components.post.more.PostMoreBottomSheet
import com.salazar.cheers.core.ui.components.share.ShareBottomSheet
import com.salazar.cheers.core.util.playback.AudioState
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
    onDrinkClick: (String) -> Unit = {},
    navigateToDeleteDialog: () -> Unit = {},
) {
    var showShareBottomSheet by remember { mutableStateOf(false) }
    var showMentionBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            name = post.name,
            drinkName = post.drinkName,
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
        if (post.photos.isNotEmpty()) {
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
        }
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
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PostDrink(
                drink = post.drinkName,
                picture = post.drinkPicture,
                colorString = post.drinkColor,
                onClick = {
                    onDrinkClick(post.drinkId)
                },
            )
            if (post.hasMentions) {
                MultiAvatarComponent(
                    avatars = post.mentionAvatars,
                    modifier = modifier,
                    onClick = {
                        showMentionBottomSheet = true
                    }
                )
            }
        }
        PostFooter(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            likeCount = post.likes,
            canLike = post.canLike,
            canComment = post.canComment,
            canShare = post.canShare,
            commentCount = post.comments,
            hasViewerLiked = post.liked,
            onLikeClick = onLikeClick,
            onLikeCountClick = onLikeCountClick,
            onCommentClick = onCommentClick,
            onShareClick = {
                showShareBottomSheet = true
            },
            commentText = post.lastCommentText,
            commentUsername = post.lastCommentUsername,
        )
    }

    if (showMentionBottomSheet) {
        MentionsBottomSheet(
            postID = post.id,
            sheetState = sheetState,
            onDismissRequest = {
                showMentionBottomSheet = false
            },
            onUserClick = onUserClick,
        )
    }
    if (showShareBottomSheet) {
        ShareBottomSheet(
            link = "",
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showShareBottomSheet = false
                }
            },
        )
    }
    if (showBottomSheet) {
        PostMoreBottomSheet(
            isAuthor = post.isAuthor,
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
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
private fun PostComponentPreviewImages(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        val user = cheersUserItem
        PostComponent(
            post = Post(
                username = user.username,
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
                lastCommentUsername = user.username,
                lastCommentText = text,
            ),
        )
    }
}
