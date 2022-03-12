package com.salazar.cheers.components.story

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.GreyYoutube

@Composable
fun YourStory(
    profilePictureUrl: String?,
    onStoryClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = rememberImagePainter(
                data = profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            modifier = Modifier
                .padding(start = 8.dp, end = 4.dp)
                .size(73.dp)
                .padding(5.dp)
                .clip(CircleShape)
                .clickable { onStoryClick() },
            contentDescription = null,
        )
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
    seen: Boolean,
    profilePictureUrl: String,
    onStoryClick: () -> Unit,
    username: String,
) {
    val border = if (seen)
        BorderStroke(1.dp, GreyYoutube)
    else
        BorderStroke(
            2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFEC01FB),
                    Color(0xFF7f00ff),
                )
            )
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onStoryClick() },
    ) {
        Image(
            painter = rememberImagePainter(
                data = profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .border(border, CircleShape)
                .size(73.dp)
                .padding(5.dp)
                .clip(CircleShape),
            contentDescription = null,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = username,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }

}
