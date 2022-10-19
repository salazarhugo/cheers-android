package com.salazar.cheers.compose.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.compose.CheersAppBar
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.extensions.noRippleClickable

@Composable
fun DirectChatBar(
    name: String,
    verified: Boolean,
    picture: String?,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit,
    onInfoClick: () -> Unit,
    onTitleClick: (username: String) -> Unit = { },
) {
    CheersAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        center = false,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(Icons.Default.ArrowBack, "")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .noRippleClickable {
                        onTitleClick(name)
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = picture)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            }).build()
                    ),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(33.dp)
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    if (name.isNotBlank())
                        Username(
                            username = name,
                            verified = verified,
                        )
                }
            }
        },
        actions = {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {})
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.search)
            )
            // Info icon
            Icon(
                imageVector = Icons.Outlined.Info,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onInfoClick)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }
    )
}