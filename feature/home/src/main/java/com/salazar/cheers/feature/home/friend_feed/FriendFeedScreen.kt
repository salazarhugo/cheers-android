package com.salazar.cheers.feature.home.friend_feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.google.android.gms.ads.nativead.NativeAd
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.WorkerState
import com.salazar.cheers.core.ui.AddFriendButton
import com.salazar.cheers.core.ui.EmptyFriendFeed
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.components.circular_progress.CircularProgressComponent
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.core.ui.components.worker.WorkerProgressComponent
import com.salazar.cheers.core.ui.text.HomeTitle
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.feature.home.NoteList
import com.salazar.cheers.feature.home.ads.NativeAdView
import com.salazar.cheers.feature.home.home.HomeUIAction
import com.salazar.cheers.feature.home.home.HomeUiState
import com.salazar.cheers.shared.data.mapper.toWorkerState

@Composable
internal fun FriendFeedScreen(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val workName = Constants.POST_UNIQUE_WORKER_NAME
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData(workName)
        .observeAsState()
        .value
    val uploadInfo = workInfos?.firstOrNull()
    val state = uploadInfo?.state

    LazyColumn(
        state = uiState.listState,
    ) {
        notes(
            uiState = uiState,
            onHomeUIAction = onHomeUIAction,
        )

        item {
            WhatsUpSection(
                avatar = uiState.account?.picture.orEmpty(),
                onClick = { onHomeUIAction(HomeUIAction.OnCreatePostClick) },
            )
            HorizontalDivider(
                thickness = 0.5.dp,
            )
        }

        uploadingSection(
            workerState = state?.toWorkerState(),
            onCancelClick = { workManager.cancelUniqueWork(workName) }
        )

        spotlight(
            parties = uiState.spotlight,
            onHomeUIAction = onHomeUIAction,
        )

        if (uiState.tickets.isNotEmpty()) {
            tickets(
                uiState = uiState,
                onHomeUIAction = onHomeUIAction,
            )
        }

        posts(
            posts = uiState.posts,
            audioState = uiState.audioState,
            audioPostID = uiState.audioPostID,
            isLoading = uiState.isLoading,
            endReached = uiState.endReached,
            nativeAd = uiState.nativeAd,
            isSignedIn = uiState.account != null,
            onHomeUIAction = onHomeUIAction,
        )

        item {
            if (uiState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressComponent()
                }
            }
        }

        suggestions(
            suggestions = uiState.suggestions,
            onHomeUIAction = onHomeUIAction,
        )
    }
}

private fun LazyListScope.partiesBanner(onClick: () -> Unit) {
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onClick,
        ) {
            TextButton(onClick = onClick) {
                Text(text = "Parties")
            }
        }
    }
}

private fun LazyListScope.emptyPosts(
    isSignedIn: Boolean,
    onClick: () -> Unit = {},
) {
    item {
        EmptyFriendFeed(
            isSignedIn = isSignedIn,
            modifier = Modifier
                .animateItem()
                .padding(vertical = 60.dp),
            onClick = onClick,
        )
    }
}

private fun LazyListScope.suggestions(
    suggestions: List<UserItem>?,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (suggestions == null)
        return

    items(
        items = suggestions,
    ) { user ->
        UserItem(
            userItem = user,
            onClick = {},
            content = {
                AddFriendButton(
                    requestedByViewer = user.requested,
                    onAddFriendClick = {
                        onHomeUIAction(HomeUIAction.OnAddFriendClick(user.id))
                    },
                    onCancelFriendRequestClick = {},
                    onDelete = {},
                )
            }
        )
    }
}

private fun LazyListScope.spotlight(
    parties: List<Party>,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (parties.isEmpty()) return

    item {
        HomeTitle(
            modifier = Modifier
                .animateItem()
                .padding(16.dp),
            text = stringResource(id = com.salazar.cheers.core.ui.R.string.spotlight)
        )
    }
}

private fun LazyListScope.tickets(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    item {
        HomeTitle(
            modifier = Modifier
                .animateItem()
                .padding(16.dp),
            text = stringResource(id = com.salazar.cheers.core.ui.R.string.tickets)
        )
    }
}

private fun LazyListScope.notes(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    item {
        NoteList(
            picture = uiState.account?.picture,
            username = uiState.account?.username,
            name = uiState.account?.name,
            notes = uiState.notes,
            modifier = Modifier.animateItem(),
            onCreateNoteClick = { onHomeUIAction(HomeUIAction.OnCreateNoteClick) },
            onNoteClick = { onHomeUIAction(HomeUIAction.OnNoteClick(it)) },
            onUserClick = { onHomeUIAction(HomeUIAction.OnUserClick(it)) }
        )
        HorizontalDivider(
            thickness = 0.5.dp,
        )
    }
}

private fun LazyListScope.posts(
    audioPostID: String,
    audioState: AudioState,
    posts: List<Post>,
    nativeAd: NativeAd?,
    isLoading: Boolean,
    isSignedIn: Boolean,
    endReached: Boolean,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (posts.isEmpty() && isLoading.not()) {
        emptyPosts(
            isSignedIn = isSignedIn,
            onClick = {
                onHomeUIAction(HomeUIAction.OnCreatePostClick)
            },
        )
    }

    items(
        count = posts.size,
        key = { posts[it].id },
    ) { i ->
        if (i != 0 && i % 4 == 0 && nativeAd != null) {
            NativeAdView(
                ad = nativeAd,
            )
        }
        val post = posts[i]
        if (i >= posts.size - 1 && !endReached && !isLoading) {
            LaunchedEffect(Unit) {
                onHomeUIAction(HomeUIAction.OnLoadNextItems)
            }
        }

        val isAudioFocused = post.id == audioPostID

        PostComponent(
            post = post,
            audioState = audioState.copy(
                isAudioPlaying = isAudioFocused && audioState.isAudioPlaying,
                audioProgress = if (isAudioFocused) audioState.audioProgress else 0f,
            ),
            modifier = Modifier.animateItem(),
            onAudioClick = {
                onHomeUIAction(HomeUIAction.OnAudioClick(post.id, post.audioUrl))
            },
            onUserClick = { userID ->
                onHomeUIAction(HomeUIAction.OnUserClick(userID))
            },
            onLikeClick = {
                onHomeUIAction(HomeUIAction.OnLikeClick(post))
            },
            onLikeCountClick = {
                onHomeUIAction(HomeUIAction.OnPostLikesClick(post.id))
            },
            onCommentClick = {
                onHomeUIAction(HomeUIAction.OnPostCommentClick(post.id))
            },
            navigateToDeleteDialog = {
                onHomeUIAction(HomeUIAction.OnDeletePostClick(post.id))
            },
            onDrinkClick = {
                onHomeUIAction(HomeUIAction.OnDrinkClick(it))
            }
        )
    }
}

@Composable
private fun WhatsUpSection(
    avatar: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarComponent(
            avatar = avatar,
            size = 36.dp,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "What's up party people?",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onClick) {
            Icon(
                Icons.Outlined.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

//@Composable
//fun Stories(
//    uiState: HomeUiState,
//    onStoryClick: (Int) -> Unit,
//    onAddStoryClick: () -> Unit,
//) {
//    val userWithStoriesList = uiState.userWithStoriesList
//    val profilePictureUrl = uiState.user?.picture
////    val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }
//
//    LazyRow(
//        state = rememberLazyListState(),
//        modifier = Modifier.padding(bottom = 8.dp),
//    ) {
//        val user = uiState.user
//        if (user != null)
//            item {
//                YourStory(
//                    profilePictureUrl = profilePictureUrl,
//                    onClick = {
//                        if (user.storyState == com.salazar.cheers.core.model.StoryState.SEEN || user.storyState == com.salazar.cheers.core.model.StoryState.NOT_SEEN)
//                            onStoryClick(0)
//                        else
//                            onAddStoryClick()
//                    },
//                    storyState = user.storyState,
//                )
//            }
//
//        itemsIndexed(
//            items = userWithStoriesList,
//            key = { _, userWithStories: UserWithStories ->
//                userWithStories.user.id
//            },
//        ) { i, userWithStories ->
//            val user = userWithStories.user
//            val stories = userWithStories.stories
//            val viewed = remember(userWithStories.stories) {
//                stories.all { it.viewed }
//            }
//
//            Story(
//                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
//                username = user.username,
//                viewed = viewed,
//                picture = user.picture,
//                onStoryClick = { onStoryClick(i) },
//            )
//        }
//    }
//}

fun LazyListScope.uploadingSection(
    workerState: WorkerState?,
    onCancelClick: () -> Unit,
) {
    if (workerState == null || workerState.isFinished) {
        return
    }

    item(key = "uploading") {
        WorkerProgressComponent(
            workerState = workerState,
            modifier = Modifier.animateItem(),
            onCancelClick = onCancelClick,
        )
        HorizontalDivider()
    }
}
