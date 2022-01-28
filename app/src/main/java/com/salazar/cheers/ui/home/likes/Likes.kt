package com.salazar.cheers.ui.home.likes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.components.FollowButton
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography

@Composable
fun LikesScreen(
    uiState: LikesViewModelState,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(navigateBack = navigateBack) }
    ) {
        Column {
            if (uiState.isLoading)
                LoadingScreen()
            val users = uiState.users
            if (users != null)
                UserList(users = users)
        }
    }
}

@Composable
fun Toolbar(
    navigateBack: () -> Unit,
) {
    Column {
        SmallTopAppBar(
            title = {
                Text(
                    "Likes",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                )
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Outlined.ArrowBack, "")
                }
            },
        )
    }
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserCard(user)
        }
    }
}

@Composable
fun UserCard(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(data = user.profilePictureUrl),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.fullName.isNotBlank())
                    Text(text = user.fullName, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium
                )
//                    Text(text = user.username, style = Typography.bodyMedium)
            }
        }

        FollowButton(isFollowing = true, onClick = { /*TODO*/ })
    }
}
