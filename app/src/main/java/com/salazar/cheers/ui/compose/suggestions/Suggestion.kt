package com.salazar.cheers.ui.compose.suggestions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.salazar.cheers.R
import com.salazar.cheers.internal.SuggestionUser
import kotlin.math.absoluteValue


@Composable
fun Suggestion(
    suggestedUser: SuggestionUser,
    scope: PagerScope,
    page: Int
) {
    scope.apply {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp,
            tonalElevation = 8.dp,
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    // We animate the scaleX + scaleY, between 85% and 100%
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                }
            ) {

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = suggestedUser.user.picture)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            }).build()
                    ),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = suggestedUser.user.username,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = suggestedUser.user.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(
                    modifier = Modifier.padding(0.dp, 12.dp)
                ) {
                    repeat(3) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                suggestedUser.posts.getOrNull(it)?.photos?.get(
                                    0
                                )
                            ),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.0f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Text(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.padding(16.dp)

                ) {
                    Text("Follow")
                }
            }
        }
    }
}

