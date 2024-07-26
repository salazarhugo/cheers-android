package com.salazar.cheers.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R

@Composable
fun CheersSplashScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.ic_splash),
            modifier = Modifier.size(56.dp),
            contentDescription = null,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "We are setting up your account...",
            style = MaterialTheme.typography.bodyMedium,
        )
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}
