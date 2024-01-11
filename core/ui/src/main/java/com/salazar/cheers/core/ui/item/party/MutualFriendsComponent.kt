package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.MultipleAnnotation
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.components.multi_avatar.MultiAvatarComponent

@Composable
fun MutualFriendsComponent(
    users: Map<String, String>,
    modifier: Modifier = Modifier,
    max: Int = 3,
) {
    val otherCount = users.size - max
    if (users.isEmpty())
        return

    MultiAvatarComponent(
        avatars = users.keys.toList(),
        modifier = modifier,
        max = max,
    ) {
        val text = users.values.joinToString(", ")
        val plurial = if (users.size > 1) "are" else "is"
        val end =
            if (otherCount > 0) " and $otherCount ${if (otherCount > 1) "others" else "other"} are going" else " $plurial going"

        Text(
            text = text + end,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@ComponentPreviews
@Composable
private fun MutualFriendsComponentPreview() {
    CheersPreview {
        MutualFriendsComponent(
            users = mapOf("esf" to "cheers", "afw" to "mcdo", "wf" to "nike"),
        )
    }
}