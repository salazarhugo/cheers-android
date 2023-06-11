package com.salazar.cheers.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PartiesTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val icon = when (isDarkTheme) {
        true -> R.drawable.ic_cheers_logo
        false -> R.drawable.ic_cheers_logo
    }
    Column {
        TopAppBar(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            colors = TopAppBarDefaults.mediumTopAppBarColors(),
            navigationIcon = {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(34.dp),
                    contentDescription = null,
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Paris",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                    )
                }
            }
        )
//        ChipGroup(
//            users = listOf(
//                "For you",
//                "Local",
//                "This week",
//                "Friends",
//                "Groups",
//                "Online",
//                "Following"
//            )
//        )
    }
}