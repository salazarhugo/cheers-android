package com.salazar.cheers.ui.main.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.compose.items.UserItem
import com.salazar.cheers.compose.post.PostBody
import com.salazar.cheers.compose.post.PostHeader
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.detail.PostFooter


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
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
        Column {
            if (uiState.selectedPost != null)
                Post(
                    post = uiState.selectedPost,
                    onUserClick = onUserClick,
                )
        }
    }
}

@Composable
fun Post(
    post: Post,
    onUserClick: (String) -> Unit,
) {
    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
        item {
            PostHeader(
                username = post.username,
                verified = post.verified,
                beverage = Beverage.fromName(post.beverage),
                public = post.privacy == Privacy.PUBLIC.name,
                locationName = post.locationName,
                picture = post.profilePictureUrl,
                onHeaderClicked = { onUserClick(post.username) },
                onMoreClicked = {},
                created = post.created,
            )
            PostBody(
                post = post,
                onPostClicked = {},
                onLike = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            PostFooter(post = post, false, onDelete = {}, onToggleLike = {})
        }
        item {
            Text(
                text = "Host",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 8.dp),
            )
        }
        item {
            Text(
                text = "With",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            )
        }
    }
}