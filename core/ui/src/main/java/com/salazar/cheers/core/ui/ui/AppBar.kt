package com.salazar.cheers.core.ui.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
fun AppBar(
    title: String,
    center: Boolean = false,
    backNavigation: Boolean = false,
    onNavigateBack: () -> Unit = {},
) {
    if (center) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    ),
                )
            },
            navigationIcon = {
                if (backNavigation)
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
            },
//            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primary,
//                titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//            )
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    ),
                )
            },
            navigationIcon = {
                if (backNavigation)
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }
}