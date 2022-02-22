package com.salazar.cheers.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.animations.AnimateVisibilityFade

@Composable
fun MultipleAnnotation(modifier: Modifier) {
    AnimateVisibilityFade(modifier = modifier) {
        Surface(
            modifier = Modifier
                .padding(top = 16.dp, end = 32.dp)
                .clickable {},
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
        ) {
            Icon(
                Icons.Filled.ContentCopy,
                modifier = Modifier
                    .padding(6.dp)
                    .size(15.dp),
                contentDescription = null
            )
        }
    }
}

@Composable
fun InThisPhotoAnnotation(modifier: Modifier) {
    AnimateVisibilityFade(modifier = modifier) {
        Surface(
            modifier = Modifier
                .padding(top = 16.dp, end = 32.dp)
                .clickable {},
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                modifier = Modifier
                    .padding(6.dp)
                    .size(15.dp),
                contentDescription = null
            )
        }
    }
}

