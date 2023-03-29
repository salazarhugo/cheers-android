package com.salazar.cheers.map.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.map.domain.models.UserLocation
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.theme.GreenGoogle


@Composable
fun UserMapDialog(
    userLocation: UserLocation,
    onClose: () -> Unit,
    onChatClick: (String) -> Unit,
) {
    UserLocationItem(
        userLocation = userLocation,
        onClose = onClose,
        onChatClick = onChatClick,
    )
}

@Composable
fun UserLocationItem(
    modifier: Modifier = Modifier,
    userLocation: UserLocation,
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
                UserProfilePicture(
                    picture = userLocation.picture,
                    onClick = {},
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
                        val timestamp = relativeTimeFormatter(epoch = userLocation.lastUpdated).text
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
                Text(
                    text = stringResource(id = R.string.share_live),
                )
            }
        }
    }
}