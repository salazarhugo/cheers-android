package com.salazar.cheers.ui.main.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.items.UserItem
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.ui.main.detail.PostFooter
import com.salazar.cheers.ui.main.home.PostBody


@Composable
fun PostMapScreen(
    uiState: MapUiState,
    onUserClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
        Column() {
            if (uiState.selectedPost != null)
                Post(
                    postFeed = uiState.selectedPost,
                    onUserClick = onUserClick,
                )
        }
    }
}

@Composable
fun Post(
    postFeed: PostFeed,
    onUserClick: (String) -> Unit,
) {
    val post = postFeed.post
    val author = postFeed.author
    val postUsers = postFeed.tagUsers

    Text(
        text = "Host",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 8.dp),
    )
    UserItem(
        user = author,
        isAuthor = true,
        onUserClick = onUserClick,
    )
    if (postUsers.isNotEmpty()) {
        Text(
            text = if (postUsers.size > 1) "Guests" else "Guest",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
        )
        LazyColumn(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(postUsers) { user ->
                UserItem(user = user, onUserClick = onUserClick)
            }
        }
    }

    PostBody(
        post = post,
        onPostClicked = {},
        onLike = {},
    )
    PostFooter(post = post, false, onDelete = {}, onToggleLike = {})
}


//@Composable
//fun PostBody(post: Post) {
//    Image(
//        painter = rememberImagePainter(data = post.photoUrl),
//        contentDescription = "avatar",
//        alignment = Alignment.Center,
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .aspectRatio(1f)// or 4/5f
//            .fillMaxWidth()
//    )
//}