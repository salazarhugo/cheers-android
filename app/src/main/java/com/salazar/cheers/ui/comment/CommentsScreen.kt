package com.salazar.cheers.ui.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.salazar.cheers.components.FunctionalityNotAvailablePanel
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    modifier: Modifier = Modifier,
) {
    Column() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = GreySheet,
            topBar = { Toolbar() },
            bottomBar = { }
        ) {
            FunctionalityNotAvailablePanel()
        }
    }
}

@Composable
fun Toolbar() {
    Column {
        SmallTopAppBar(
            title = {
                Text(
                    "Comments",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = GreySheet,
            )
        )
    }
}
@Composable
fun CommentsFooter(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommentsFooterIdle()
    }
}

@Composable
fun CommentsFooterIdle() {
    IconButton(
        onClick = {},
    ) {
        Icon(
            Icons.Default.PhotoAlbum,
            contentDescription = null,
            tint = Color.White,
        )
    }
    Text(
        text = "POST",
        textAlign = TextAlign.Center,
        color = Color.White,
    )
}