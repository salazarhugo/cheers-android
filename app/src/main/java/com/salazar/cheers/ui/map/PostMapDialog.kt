package com.salazar.cheers.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.detail.PostFooter
import com.salazar.cheers.ui.theme.Typography


@Composable
fun PostMapScreen(
    uiState: MapUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DividerM3()
        if (uiState.selectedPost != null)
            Post(uiState.selectedPost)
    }
}

@Composable
fun Post(post: Post) {
    val brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFD41668),
            Color(0xFFF9B85D),
        )
    )

    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = null),//post.creator.profilePictureUrl),
            contentDescription = "Profile image",
            modifier = Modifier
                .border(1.2.dp, brush, CircleShape)
                .size(33.dp)
                .padding(3.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(8.dp))
        Column {
//            Username(
//                username = post.creator.username,
//                verified = post.creator.verified,
//                textStyle = Typography.bodyMedium
//            )
            if (post.locationName.isNotBlank())
                Text(text = post.locationName, style = Typography.labelSmall)
        }
    }
    PostBody(post = post)
    PostFooter(post = post, false, onDelete = {})
}


@Composable
fun PostBody(post: Post) {
    Image(
        painter = rememberImagePainter(data = post.photoUrl),
        contentDescription = "avatar",
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)// or 4/5f
            .fillMaxWidth()
    )
}