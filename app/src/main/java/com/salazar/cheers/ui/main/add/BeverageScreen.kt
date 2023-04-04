package com.salazar.cheers.ui.main.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun BeverageScreen(
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            ChooseOnMapAppBar(
                onBackPressed = onBackPressed,
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
        }
    }
}

@Composable
fun ChooseOnMapAppBar(
    onBackPressed: () -> Unit,
) {
    TopAppBar(title = {
        Column {
            Text(
                text = "Choose beverage",
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
                fontSize = 14.sp
            )
            Text(
                text = "Stolichnaya",
                fontSize = 14.sp
            )
        }
    },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        actions = {
            TextButton(onClick = {
                onBackPressed()
            }) {
                Text("OK")
            }
        })
}
