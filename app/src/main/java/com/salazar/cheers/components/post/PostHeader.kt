package com.salazar.cheers.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
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
import com.salazar.cheers.internal.relativeTimeFormatter

@Composable
fun PostHeader(
    username: String,
    verified: Boolean,
    public: Boolean,
    created: Long,
    locationName: String,
    profilePictureUrl: String,
    darkMode: Boolean = false,
    onHeaderClicked: (username: String) -> Unit,
    onMoreClicked: () -> Unit,
) {
    val color = if (darkMode) Color.White else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surface)
            .clickable { onHeaderClicked(username) }
            .padding(16.dp, 11.dp),
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
                    data = profilePictureUrl,
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
                    color = color,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (locationName.isNotBlank())
                        Text(text = locationName, style = MaterialTheme.typography.labelSmall)
//                    Box(
//                        modifier = Modifier
//                            .padding(horizontal = 8.dp)
//                            .size(4.dp)
//                            .clip(CircleShape)
//                            .background(MaterialTheme.colorScheme.onBackground)
//                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = relativeTimeFormatter(timestamp = created),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 8.dp),
            )
            if (public)
                Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            Icon(
                Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onMoreClicked() },
                tint = color
            )
        }
    }
}
