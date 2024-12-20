package com.salazar.cheers.feature.map.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.GreenGoogle
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.util.isJustNow
import com.salazar.cheers.data.map.UserLocation
import com.salazar.cheers.shared.util.LocalActivity


@Composable
fun UserMapDialog(
    userLocation: UserLocation?,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onChatClick: (UserLocation) -> Unit = {},
    onUserClick: (String) -> Unit,
) {
    if (userLocation == null)
        return

    val gmmIntentUri =
        Uri.parse("geo:0,0?q=${userLocation.latitude},${userLocation.longitude}(${userLocation.name.ifBlank { userLocation.username }})")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    val activity = LocalActivity.current

    UserLocationItem(
        modifier = modifier,
        userLocation = userLocation,
        onClose = onClose,
        onChatClick = {
            onChatClick(userLocation)
        },
        onDirectionsClick = {
            activity.startActivity(mapIntent)
        },
        onUserClick = {
            onUserClick(userLocation.username)
        }
    )
}

@Composable
fun UserLocationItem(
    modifier: Modifier = Modifier,
    userLocation: UserLocation,
    onClose: () -> Unit,
    onChatClick: (String) -> Unit,
    onDirectionsClick: () -> Unit,
    onUserClick: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarComponent(
                    avatar = userLocation.picture,
                    size = 64.dp,
                    onClick = onUserClick,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Username(
                        username = userLocation.name.ifBlank { userLocation.username },
                        verified = userLocation.verified,
                        onClick = onUserClick,
                    )
                    val annotatedString = buildAnnotatedString {
                        append("Last active ")
                        val timestamp =
                            com.salazar.cheers.core.util.relativeTimeFormatter(seconds = userLocation.lastUpdated).text
                        if (isJustNow(userLocation.lastUpdated * 1000)) {
                            withStyle(style = SpanStyle(color = GreenGoogle)) {
                                append(timestamp)
                            }
                        } else {
                            append("$timestamp ago")
                        }
                    }

                    Text(
                        text = annotatedString,
                    )
                }
            }
            IconButton(
                onClick = onClose,
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = { onChatClick(userLocation.id) },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Chat icon",
                    )
                    Text(text = "Chat")
                }
            }
            FilledTonalButton(
                onClick = onDirectionsClick,
            ) {
                Icon(
                    imageVector = Icons.Default.ShareLocation,
                    contentDescription = "Share icon",
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun UserMapDialogPreview() {
    CheersPreview {
        UserMapDialog(
            userLocation = UserLocation(
                id = "",
                picture = "",
                username = "cheers",
                verified = true,
                name = "Cheers Social",
                locationName = "Aulnay Sous-Bois",
                lastUpdated = 0L,
                latitude = 0.0,
                longitude = 0.0,
            ),
            modifier = Modifier,
            onUserClick = {},
        )
    }
}
