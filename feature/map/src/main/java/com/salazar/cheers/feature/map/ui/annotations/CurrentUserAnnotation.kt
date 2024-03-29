package com.salazar.cheers.feature.map.ui.annotations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.modifier.animatedBorder
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.feature.map.R

@Composable
internal fun CurrentUserAnnotation(
    name: String,
    modifier: Modifier = Modifier,
    picture: String? = null,
    ghostMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color = when(isSelected) {
        true -> BlueCheers
        false -> Color.White
    }

    val size = when(isSelected) {
        true -> 140.dp
        false -> 100.dp
    }

    val pictureUrl = when(ghostMode) {
        true -> ""
        false -> picture
    }

    Column(
        modifier = modifier
            .size(size),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (ghostMode) {
            Image(
                imageVector = Icons.Outlined.HideSource,
                modifier = Modifier
                    .shadow(elevation = 9.dp, shape = CircleShape)
                    .border(2.dp, color, CircleShape)
                    .size(54.dp)
                    .clickable { onClick() }
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
            )
        } else {
            AvatarComponent(
                modifier = Modifier
                    .shadow(elevation = 9.dp, shape = CircleShape)
                    .animatedBorder(
                        initialColor = Color(0xFF45CA6C),
                        targetColor = Color(0xFF0AB7AE),
                        shape = CircleShape,
                        borderWidth = 2.dp,
                    )
                    .padding(4.dp)
                ,
                avatar = pictureUrl,
                onClick = onClick,
            )
        }
        Text(
            text = name,
            modifier = Modifier
                .offset(y = (-8).dp)
                .shadow(elevation = 9.dp, shape = CircleShape)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(4.dp),
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@ComponentPreviews
@Composable
private fun CurrentUserAnnotationPreview() {
    CheersPreview {
        CurrentUserAnnotation(
            name = "Me",
            ghostMode = true,
        )
        CurrentUserAnnotation(
            name = "Me",
        )
        CurrentUserAnnotation(
            name = "Me",
            isSelected = true,
        )
    }
}
