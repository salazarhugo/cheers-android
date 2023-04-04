package com.salazar.cheers.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.salazar.cheers.ui.theme.Roboto

@Preview
@Composable
fun MyTopAppBar(
    title: String = "Title",
    onPop: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = {
                onPop()
            }) {
                Icon(Icons.Default.Close, "")
            }
        },
        actions = {
            IconButton(onClick = {
                onSave()
            }) {
                Icon(Icons.Default.Check, "", tint = MaterialTheme.colorScheme.primary)
            }
        })
}
