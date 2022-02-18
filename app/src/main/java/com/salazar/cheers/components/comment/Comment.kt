package com.salazar.cheers.components.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.components.Username

@Preview
@Composable
fun CommentPreview() {
    Comment(
        profilePictureUrl = "",
        username = "cheers",
        verified = true,
        text = "J'arrive les boys",
        onLike = {},
        onReply = {},
        onCommentClicked = {},
    )
}

@Composable
fun Comment(
    profilePictureUrl: String,
    username: String,
    verified: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onCommentClicked: () -> Unit,
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCommentClicked() }
            .padding(14.dp, 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row() {
            Image(
                painter = rememberImagePainter(
                    data = profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Username(
                    username = username,
                    verified = verified,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                Text(text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "1d",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Reply",
                        modifier = Modifier.clickable { onReply() },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Icon(
            Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(CircleShape)
                .clickable { onLike() }
        )
    }
}