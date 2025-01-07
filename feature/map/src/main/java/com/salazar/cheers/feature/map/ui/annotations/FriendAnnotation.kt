package com.salazar.cheers.feature.map.ui.annotations

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.BlueCheers
import java.util.Date

@Composable
fun FriendAnnotation(
    name: String,
    username: String,
    modifier: Modifier = Modifier,
    picture: String? = null,
    isSelected: Boolean = false,
    lastUpdated: Long = Date().time / 1000,
    onClick: () -> Unit = {},
) {
    val color = when (isSelected) {
        true -> BlueCheers
        false -> Color.White
    }

    val size = when (isSelected) {
        true -> 140.dp
        false -> 100.dp
    }
    Column(
        modifier = modifier
            .size(size),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarComponent(
            modifier = Modifier
                .shadow(elevation = 9.dp, shape = CircleShape)
                .border(2.dp, color, CircleShape),
            avatar = picture,
            name = name,
            username = username,
            onClick = onClick,
        )
        if (isSelected) {
            UserAnnotationText(
                name = name.ifBlank { username },
                lastUpdated = lastUpdated,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun FriendAnnotationPreview() {
    val user = cheersUser
    CheersPreview {
        FriendAnnotation(
            name = user.name,
            username = user.username,
            modifier = Modifier,
            isSelected = true,
        )
    }
}
