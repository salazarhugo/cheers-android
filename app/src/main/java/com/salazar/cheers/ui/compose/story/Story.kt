package com.salazar.cheers.ui.compose.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cheers.type.UserOuterClass
import com.salazar.cheers.data.enums.StoryState
import com.salazar.cheers.ui.compose.share.UserProfilePicture
import com.salazar.cheers.ui.theme.BlueCheers

@Composable
fun YourStory(
    profilePictureUrl: String?,
    storyState: StoryState,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)

    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("upload_story")
        .observeAsState()
        .value

    val uploadInfo = remember(key1 = workInfos) {
        workInfos?.firstOrNull()
    }

    val storyState2 =
        if (uploadInfo?.state == WorkInfo.State.RUNNING)
            StoryState.LOADING
        else
            storyState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            UserProfilePicture(
                picture = profilePictureUrl ?: "",
                storyState = storyState2,
                size = 64.dp,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                onClick = onClick,
            )
            if (storyState == StoryState.EMPTY)
                Box(
                    modifier = Modifier
                        .offset(x = (-6).dp, y = (-6).dp)
                        .size(22.dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(BlueCheers)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        modifier = Modifier.padding(3.dp),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your story",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun Story(
    modifier: Modifier = Modifier,
    viewed: Boolean,
    picture: String?,
    username: String,
    onStoryClick: (String) -> Unit,
) {
    val state = if (viewed)
        StoryState.SEEN
    else
        StoryState.NOT_SEEN

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserProfilePicture(
            picture = picture,
            storyState = state,
            size = 64.dp,
            modifier = Modifier.padding(horizontal = 8.dp),
            onClick = { onStoryClick(username) },
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = username,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
