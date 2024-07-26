package com.salazar.cheers.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.model.User

@Composable
fun ProfileStats(
    user: User,
    modifier: Modifier = Modifier,
    onFriendsClick: () -> Unit = {},
    onDrinksClick: () -> Unit = {},
    onPartiesClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val items = listOf(
            Counter(com.salazar.cheers.core.ui.R.plurals.friends, user.friendsCount),
            Counter(com.salazar.cheers.core.ui.R.plurals.drinks, user.postCount),
            Counter(com.salazar.cheers.core.ui.R.plurals.parties, user.followers),
        )

        items.forEachIndexed { i, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    when (i) {
                        0 -> onFriendsClick()
                        1 -> onDrinksClick()
                        2 -> onPartiesClick()
                        else -> onFriendsClick()
                    }
                }
            ) {
                AnimatedTextCounter(
                    targetState = item.value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = pluralStringResource(
                        id = item.name,
                        count = item.value
                    ).replaceFirstChar(Char::titlecase),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
fun ProfileStatsPreview() {
    CheersPreview {
        ProfileStats(
            modifier = Modifier.padding(16.dp),
            user = User(
                friendsCount = 80234,
                postCount = 140,
                followers = 0,
            ),
        )
    }
}