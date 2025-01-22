package com.salazar.cheers.feature.chat.ui.components.bottombar

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatUIAction


@Composable
fun ChatBottomBarAttachments(
    images: List<Uri>,
    onChatUIAction: (ChatUIAction) -> Unit,
) {
    if (images.isEmpty()) return

    Surface(tonalElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                images.forEach {  image ->
                    AsyncImage(
                        model = image.toString(),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .aspectRatio(1f)
                        ,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            IconButton(
                onClick = {
                    onChatUIAction(ChatUIAction.OnReplyMessage(null))
                },
                modifier = Modifier
                    .padding(start = 32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun ChatBottomBarAttachmentsPreview() {
    CheersPreview {
      ChatBottomBarAttachments(
          images = listOf(Uri.EMPTY),
        ) { }
    }
}