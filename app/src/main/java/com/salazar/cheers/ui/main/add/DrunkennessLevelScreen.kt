package com.salazar.cheers.ui.main.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun DrunkennessLevelScreen(
    onBackPressed: () -> Unit,
    onSelectDrunkenness: (Int) -> Unit,
    drunkenness: Int,
    onDone: () -> Unit
) {
    Scaffold(
        topBar = {
            DrunkennessAppBar(
                onBackPressed = onBackPressed,
                onDone = onDone
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val status = when {
                drunkenness < 20 -> "Sober"
                drunkenness < 40 -> "Tipsy"
                drunkenness < 60 -> "Buzzed"
                drunkenness < 80 -> "Drunk"
                drunkenness < 100 -> "Wasted"
                else -> "Dead"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = drunkenness.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Slider(
                value = drunkenness.toFloat(),
                onValueChange = { onSelectDrunkenness(it.toInt()) },
                valueRange = 0f..101f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@Composable
fun DrunkennessAppBar(
    onBackPressed: () -> Unit,
    onDone: () -> Unit,
) {
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        title = {
            Text(
                text = "Drunkenness level",
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
                fontSize = 14.sp
            )
        },
        actions = {
            TextButton(onClick = onDone) {
                Text("OK")
            }
        },
    )
}
