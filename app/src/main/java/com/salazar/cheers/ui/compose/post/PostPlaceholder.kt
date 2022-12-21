package com.salazar.cheers.ui.compose.post

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PostPlaceholder() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = "Post placeholder",
    )
}