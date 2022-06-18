package com.salazar.cheers.components.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Comment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Comment(
    comment: Comment,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onDeleteComment: (String) -> Unit,
    onCommentClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCommentClicked() }
            .padding(14.dp, 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = comment.profilePictureUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Username(
                        username = comment.username,
                        verified = comment.verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(comment.text)
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val dateFormatter = SimpleDateFormat("HH:mm")
                    Text(
                        text = dateFormatter.format(comment.created),
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
        if (comment.authorId == FirebaseAuth.getInstance().currentUser?.uid)
//        TODO("LIKE COMMENT")
            Icon(
                Icons.Default.DeleteOutline,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(CircleShape)
                    .clickable { onDeleteComment(comment.id) }
            )
//        TODO("LIKE COMMENT")
//        Icon(
//            Icons.Default.FavoriteBorder,
//            contentDescription = null,
//            modifier = Modifier
//                .padding(top = 16.dp)
//                .clip(CircleShape)
//                .clickable { onLike() }
//        )
    }
}