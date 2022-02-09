package com.salazar.cheers.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.components.Username

@Composable
fun PostHeader(
    username: String,
    verified: Boolean,
    locationName: String,
    onHeaderClicked: (username: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp, 11.dp)
            .clickable { onHeaderClicked(username) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            val brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD41668),
                    Color(0xFFF9B85D),
                )
            )

            Image(
                painter = rememberImagePainter(
                    data = null,//post.creator.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
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
                Username(
                    username = username,
                    verified = verified,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                if (locationName.isNotBlank())
                    Text(text = locationName, style = MaterialTheme.typography.labelSmall)
            }
        }
        Icon(Icons.Default.MoreVert, "", modifier = Modifier.clickable {
        })
    }
}
