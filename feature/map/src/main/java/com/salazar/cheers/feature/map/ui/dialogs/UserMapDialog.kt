package com.salazar.cheers.feature.map.ui.dialogs

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
import androidx.compose.material3.MaterialTheme
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


@Composable
fun UserMapDialog(
    userLocation: com.salazar.cheers.data.map.UserLocation?,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
) {
    if (userLocation == null)
        return

    UserLocationItem(
        modifier = modifier,
        userLocation = userLocation,
        onClose = onClose,
        onChatClick = onChatClick,
    )
}

@Composable
fun UserLocationItem(
    modifier: Modifier = Modifier,
    userLocation: com.salazar.cheers.data.map.UserLocation,
    onClose: () -> Unit,
    onChatClick: (String) -> Unit,
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
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    if (userLocation.name.isNotBlank())
                        Text(
                            text = userLocation.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    val annotatedString = buildAnnotatedString {
                        append(userLocation.locationName)
                        val timestamp =
                            com.salazar.cheers.core.util.relativeTimeFormatter(seconds = userLocation.lastUpdated).text
                        if (timestamp == "just now") {
                            withStyle(style = SpanStyle(color = GreenGoogle)) {
                                append(timestamp)
                            }
                        } else {
                            append(timestamp)
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalButton(
                onClick = { onChatClick(userLocation.id) },
            ) {
                Icon(Icons.Default.ChatBubble, contentDescription = null)
            }
            FilledTonalButton(
                onClick = {},
            ) {
                Icon(Icons.Default.ShareLocation, contentDescription = null)
//                Text(
//                    text = stringResource(id = R.string.share_live),
//                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun UserMapDialogPreview() {
    CheersPreview {
        UserMapDialog(
            userLocation = com.salazar.cheers.data.map.UserLocation(
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
        )
    }
}
