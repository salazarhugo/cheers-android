package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
fun Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            AnimatedTextCounter(
                targetState = title,
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = actions,
    )
}

