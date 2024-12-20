package com.salazar.cheers.feature.map.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.data.map.UserLocation
import com.salazar.cheers.data.map.cheersUserLocation
import com.salazar.cheers.data.map.cheersUserLocationList

@Composable
fun MapFriendListComponent(
    userLocation: UserLocation,
    friends: List<UserLocation>,
    modifier: Modifier = Modifier,
    onUserClick: (UserLocation) -> Unit,
) {
    val quickFilters = listOf(
        "Parties",
        "Favorites",
    )
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = ZeroCornerSize,
            bottomStart = ZeroCornerSize,
        ),
        tonalElevation = BottomSheetDefaults.Elevation,
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item(
                key = userLocation.id,
            ) {
                AvatarComponent(
                    modifier = Modifier.animateItem(),
                    avatar = userLocation.picture,
                    onClick = { onUserClick(userLocation) },
                )
            }
            items(
                items = friends,
            ) {
                AvatarComponent(
                    modifier = Modifier.animateItem(),
                    avatar = it.picture,
                    onClick = { onUserClick(it) },
                )
            }
            items(
                items = quickFilters,
            ) {
                QuickFilter(
                    text = it,
                    modifier = Modifier.animateItem(),
                    onClick = {},
                )
            }
        }
    }
}

@Composable
fun QuickFilter(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .clickable { onClick() }
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(BlueCheers)
            ,
            imageVector = Icons.Rounded.Star,
            contentDescription = "Star icon",
            tint = Color.White,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}


@Preview
@Composable
private fun MapFriendListComponentPreview() {
    CheersPreview {
        MapFriendListComponent(
            friends = cheersUserLocationList.take(3),
            onUserClick = {},
            userLocation = cheersUserLocation,
        )
    }
}