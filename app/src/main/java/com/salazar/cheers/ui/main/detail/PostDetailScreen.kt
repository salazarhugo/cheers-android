package com.salazar.cheers.ui.main.detail

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.PostCaption
import com.salazar.cheers.core.ui.StaticMap
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.post.PostMedia
import com.salazar.cheers.core.ui.components.post.PostHeader
import com.salazar.cheers.core.ui.item.LikeButton
import com.salazar.cheers.core.ui.theme.Roboto
import java.util.Date

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState.HasPost,
    onBackPressed: () -> Unit,
    onHeaderClicked: (username: String) -> Unit,
    onDelete: () -> Unit,
    onLeave: () -> Unit,
    onMessageClicked: () -> Unit,
    onMapClick: () -> Unit,
    onToggleLike: (com.salazar.cheers.data.post.repository.Post) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val post = uiState.postFeed
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(title = { 
                Text(
                    text = stringResource(id = R.string.post),
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                )
              },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                })
        }
    ) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(top = it.calculateTopPadding()),
        ) {
            Post(
                post = uiState.postFeed,
                members = uiState.members,
                onHeaderClicked = onHeaderClicked,
                onDelete = onDelete,
                isAuthor = post.authorId == FirebaseAuth.getInstance().currentUser?.uid!!,
                onMapClick = onMapClick,
                onToggleLike = onToggleLike,
                onLeave = onLeave,
                onMessageClicked = onMessageClicked,
                onUserClick = onUserClick,
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
fun PostDetails(
    privacy: Privacy,
    createTime: Long,
    drunkenness: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sharing with",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(privacy.icon, null)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(privacy.title)
                    Text(privacy.subtitle)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text(
                text = Date(createTime.toLong()).toString(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Drunkenness level $drunkenness",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun Post(
    post: com.salazar.cheers.data.post.repository.Post,
    members: List<com.salazar.cheers.core.model.UserItem>?,
    onHeaderClicked: (username: String) -> Unit,
    onLeave: () -> Unit,
    onDelete: () -> Unit,
    onUserClick: (String) -> Unit,
    onMapClick: () -> Unit,
    onMessageClicked: () -> Unit,
    onToggleLike: (com.salazar.cheers.data.post.repository.Post) -> Unit,
    isAuthor: Boolean,
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val joined = post.tagUsersId.contains(uid)

    Column {
        LazyColumn {

            item {
                PostHeader(
                    isPublic = post.privacy == Privacy.PUBLIC.name,
                    username = post.username,
                    verified = post.verified,
                    avatar = post.profilePictureUrl,
                    locationName = post.locationName,
                    createTime = post.createTime,
                    onUserClick = { },
                    onMoreClick = {},
                )
                PostCaption(
                    caption = post.caption,
                    onUserClicked = onUserClick,
                    onPostClicked = {},
                )
                PostMedia(
                    medias = post.photos.map { Media.Image(uri = Uri.parse(it)) },
                    onPostClick = {},
                )
                PostFooter(
                    post = post,
                    onDelete = onDelete,
                    isAuthor = isAuthor,
                    onToggleLike = onToggleLike,
                )
            }
            item {
                Text(
                    text = "Drinkers",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            if (members != null)
                items(
                    items = members,
                    key = { it.id },
                ) { user ->
                    UserItem(
                        userItem = user,
                        onClick = { onUserClick(user.username) },
                    ) {
                        FriendButton(
                            isFriend = user.friend,
                            onClick = {},
                        )
                    }
                }
            if (post.locationName.isNotBlank())
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "Hangout event",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Column {
//                        Text(
//                            text = String.format("%.2f", post.locationLongitude),
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = String.format("%.2f", post.locationLatitude),
//                            style = MaterialTheme.typography.titleMedium
//                        )
                        }
                    }
                    StaticMap(
                        longitude = post.longitude,
                        latitude = post.latitude,
                        onMapClick = onMapClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .aspectRatio(1f),
                    )
                }
            item {
                PostDetails(
                    privacy = Privacy.valueOf(post.privacy),
                    createTime = post.createTime,
                    drunkenness = post.drunkenness,
                )
            }

            item {
                Buttons(
                    joined = joined,
                    onLeave = onLeave,
                    onMessageClicked = onMessageClicked
                )
            }
        }
    }
}

@Composable
fun Buttons(
    joined: Boolean,
    onLeave: () -> Unit,
    onMessageClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (joined) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onLeave,
            ) {
                Text("Leave")
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onMessageClicked,
        ) {
            Text("Message")
        }
    }
}

@Composable
fun PostFooter(
    post: com.salazar.cheers.data.post.repository.Post,
    isAuthor: Boolean,
    onDelete: () -> Unit,
    onToggleLike: (com.salazar.cheers.data.post.repository.Post) -> Unit,
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
                onToggle = { onToggleLike(post) },
            )
            Icon(painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon), "")
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
