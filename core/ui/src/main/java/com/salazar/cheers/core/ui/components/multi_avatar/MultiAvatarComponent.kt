package com.salazar.cheers.core.ui.components.multi_avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import kotlin.math.min

@Composable
fun MultiAvatarComponent(
    avatars: List<String>,
    modifier: Modifier = Modifier,
    max: Int = 3,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row {
            Box {
                avatars.take(max).forEachIndexed { i, avatar ->
                    AvatarComponent(
                        modifier = Modifier
                            .offset(x = i * 13.dp)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                        avatar = avatar,
                        size = 26.dp,
                        onClick = onClick,
                    )
                }
                if (avatars.count() > max) {
                    Text(
                        text = "+${avatars.count() - max}",
                        modifier = Modifier
                            .offset(x = max * 14.dp)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .height(26.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.width((min(avatars.size, max) - 1) * 13.dp))
        }
        content()
    }
}

@ComponentPreviews
@Composable
private fun AvatarComponentPreview() {
    CheersPreview {
        MultiAvatarComponent(
            avatars = listOf(
                "cheers",
                "mcdo",
                "nike",
            ),
            modifier = Modifier.padding(16.dp),
            max = 2,
        )
    }
}
