package com.salazar.cheers.components.share

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun Toolbar(
    title: String,
    onBackPressed: () -> Unit,
) {
    SmallTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
    )
}

