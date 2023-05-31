package com.salazar.cheers.notes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.notes.domain.models.Note
import com.salazar.cheers.core.ui.animations.Bounce

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (String) -> Unit,
) {
    val placeHolder: Int = R.drawable.default_profile_picture

    Bounce(
        onBounce = { onClick(note.userId) },
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.width(88.dp),
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = note.picture)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                                error(placeHolder)
                                fallback(placeHolder)
                            }).build()
                    ),
                    contentDescription = "Profile picture",
                    modifier = modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
            Text(
                text = note.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}