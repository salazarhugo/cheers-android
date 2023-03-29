package com.salazar.cheers.map.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.post.ui.item.PostBody
import com.salazar.cheers.post.ui.item.PostHeader
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.map.screens.map.MapUiState
import com.salazar.cheers.post.ui.item.PostItem
import com.salazar.cheers.ui.main.detail.PostFooter


@Composable
fun PostMapScreen(
    uiState: MapUiState,
) {
    if (uiState.selectedPost != null)
        PostItem(
            post = uiState.selectedPost,
            onHomeUIAction = {},
        )
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
                createTime = post.createTime,
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