package com.salazar.cheers.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun EmptyActivity() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = "No activity yet",
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

