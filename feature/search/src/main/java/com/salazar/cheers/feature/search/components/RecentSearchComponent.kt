package com.salazar.cheers.feature.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.model.mirageParty
import com.salazar.cheers.core.model.toUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
internal fun RecentSearchComponent(
    recentSearch: RecentSearch,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onClick: (RecentSearch) -> Unit,
) {
    when (recentSearch) {
        is RecentSearch.Party -> Unit
        is RecentSearch.User -> {
            UserItem(
                userItem = recentSearch.toUserItem(),
                onClick = { onClick(recentSearch) },
                content = {
                    IconButton(
                        onClick = { onDeleteRecentUser(recentSearch) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close icon",
                        )
                    }
                }
            )
        }

        is RecentSearch.Text -> {
            RecentTextSearch(
                text = recentSearch.text,
                onClick = { onClick(recentSearch) },
                onDeleteClick = { onDeleteRecentUser(recentSearch) },
            )
        }
    }
}

@Composable
private fun RecentTextSearch(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(text) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(54.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .padding(8.dp),
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search icon",
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        IconButton(
            onClick = { onDeleteClick(text) },
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close icon",
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun RecentTextSearchComponentPreview() {
    val recentSearches = listOf(
        RecentSearch.Text(text = "joaquim_leite"),
        RecentSearch.User(user = cheersUserItem),
        RecentSearch.Party(party = mirageParty),
    )
    CheersPreview {
        recentSearches.forEach {
            RecentSearchComponent(
                recentSearch = it,
                onDeleteRecentUser = {},
                onClick = {},
            )
        }
    }
}
