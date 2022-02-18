package com.salazar.cheers.components.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.share.UserProfilePicture
import com.salazar.cheers.internal.User

@Composable
fun UserItem(
    user: User,
    isAuthor: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfilePicture(profilePictureUrl = user.profilePictureUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.fullName.isNotBlank())
                    Text(text = user.fullName, style = MaterialTheme.typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
//                    Text(text = user.username, style = Typography.bodyMedium)
            }
        }
        if (isAuthor)
            Image(
                rememberImagePainter(R.drawable.ic_crown),
                modifier = Modifier.padding(end = 8.dp).size(16.dp),
                contentDescription = null,
            )
//        FollowButton(isFollowing = true, onClick = { /*TODO*/ })
    }
}
