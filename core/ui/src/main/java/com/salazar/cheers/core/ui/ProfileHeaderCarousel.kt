package com.salazar.cheers.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent

@Composable
fun ProfileHeaderCarousel(
    user: User,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    onBannerClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onDeleteBannerClick: (String) -> Unit = {},
) {
    val items = user.banner
    val avatar = user.picture
    val avatarSize = 110.dp
    val bannerCornerSize = 16.dp
    val bannerShape = RoundedCornerShape(bannerCornerSize)
    val avatarOffset = avatarSize / 2 - bannerCornerSize

    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(16.dp)),
        ) {
            ProfileBanner(
                modifier = Modifier,
                banner = "",
                onEditClick = onBannerClick,
                clickable = editable,
                ratio = 1f,
                onDeleteClick = {}
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                AvatarComponent(
                    avatar = avatar,
                    name = user.name,
                    modifier = Modifier
                        .border(5.dp, MaterialTheme.colorScheme.background, CircleShape),
                    size = 110.dp,
                    onClick = {
//                        if (isEditable.not()) {
//                            openAlertDialog = true
//                        }
                        onAvatarClick()
                    },
                )
                ProfileName(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                ),
                            )
                            drawContent()
                        }
                        .padding(32.dp),
                    name = user.name,
                    jobCompany = user.jobCompany,
                    jobTitle = user.jobTitle,
                    education = user.education,
                    isBusinessAccount = user.isBusinessAccount,
                )
            }
        }
    } else {
        val state = rememberCarouselState() { items.size }
        Box(
            modifier = Modifier.padding(bottom = avatarOffset),
            contentAlignment = Alignment.BottomStart,
        ) {
            HorizontalMultiBrowseCarousel(
                state = state,
                modifier = modifier,
                preferredItemWidth = 500.dp,
                itemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) { i ->
                val item = items.getOrNull(i) ?: return@HorizontalMultiBrowseCarousel

                Box(
                    modifier = Modifier
                        .maskClip(shape = bannerShape),
                ) {
                    ProfileBanner(
                        modifier = Modifier,
                        banner = item,
                        onEditClick = onBannerClick,
                        clickable = editable,
                        ratio = 1f,
                        onDeleteClick = {
                            onDeleteBannerClick(item)
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(avatarSize)
                            .drawWithContent {
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                    ),
                                )
                                drawContent()
                            }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                AvatarComponent(
                    avatar = avatar,
                    name = user.name,
                    modifier = Modifier
                        .offset(y = avatarOffset)
                        .border(6.dp, MaterialTheme.colorScheme.background, CircleShape),
                    size = avatarSize,
                    onClick = {
//                        if (isEditable.not()) {
//                            openAlertDialog = true
//                        }
                        onAvatarClick()
                    },
                )
                ProfileName(
                    modifier = Modifier
//                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 16.dp)
                    ,
                    name = user.name,
                    jobCompany = user.jobCompany,
                    jobTitle = user.jobTitle,
                    education = user.education,
                    isBusinessAccount = user.isBusinessAccount,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileBannerAndAvatarPreview() {
    CheersPreview {
        ProfileHeaderCarousel(
            modifier = Modifier.padding(16.dp),
            user = cheersUser.copy(banner = listOf("")),
        )
    }
}

@Composable
fun ProfileName(
    name: String,
    jobTitle: String,
    jobCompany: String,
    education: String,
    isBusinessAccount: Boolean,
    modifier: Modifier = Modifier,
) {
    if (name.isBlank()) return

    val job = if (jobCompany.isNotBlank()) {
        "$jobTitle at $jobCompany"
    } else {
        jobTitle
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            if (isBusinessAccount) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (job.isNotBlank()) {
                Text(
                    text = job,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
            }
            if (education.isNotBlank()) {
                Text(
                    text = education,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
            }
        }
    }
}

