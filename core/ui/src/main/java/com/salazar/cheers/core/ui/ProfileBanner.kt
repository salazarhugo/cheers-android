package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.salazar.cheers.core.util.Utils.conditional

@Composable
fun ProfileBanner(
    modifier: Modifier = Modifier,
    banner: String? = null,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .aspectRatio(3f)
                .background(Color.DarkGray)
                .conditional(clickable) {
                    this.clickable {
                        onClick()
                    }
                    .background(Color.Black)
                    .alpha(0.6f)
                },
            model = banner,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        if (clickable)
            Icon(Icons.Default.Edit, contentDescription = null)
    }
}