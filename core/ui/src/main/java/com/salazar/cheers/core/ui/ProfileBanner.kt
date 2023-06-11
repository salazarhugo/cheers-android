package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ProfileBanner(
    modifier: Modifier = Modifier,
    banner: String? = null,
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f),
        model = banner,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}