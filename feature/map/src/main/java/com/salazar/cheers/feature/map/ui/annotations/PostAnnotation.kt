package com.salazar.cheers.feature.map.ui.annotations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.BlueCheers

@Composable
fun PostAnnotation(
    post: Post,
    modifier: Modifier = Modifier,
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

    val picture = post.drinkPicture

    Column(
        modifier = modifier
            .size(size),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Bounce(
            onBounce = onClick,
        ) {
            AvatarComponent(
                avatar = picture,
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                placeHolder = com.salazar.cheers.core.ui.R.drawable.beer,
                backgroundColor = Color.Transparent,
                size = 48.dp,
            )
        }
        Text(
            text = post.name.ifBlank { post.username },
            modifier = Modifier
                .offset(y = (-8).dp)
                .shadow(elevation = 9.dp, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(4.dp),
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}


@ComponentPreviews
@Composable
private fun PostAnnotationPreview() {
    CheersPreview {
        PostAnnotation(
            post = Post(name = "Lars Salazar"),
            modifier = Modifier,
        )
    }
}
