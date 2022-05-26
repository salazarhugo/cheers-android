package com.salazar.cheers.ui.main.event.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.event.EventDetails
import com.salazar.cheers.ui.main.add.LocationSection
import com.salazar.cheers.ui.main.add.ShareButton
import com.salazar.cheers.ui.main.event.add.CategorySection
import com.salazar.cheers.ui.main.event.add.Description
import com.salazar.cheers.ui.main.event.add.TopAppBar

@Composable
fun EditEventScreen(
    uiState: EditEventUiState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val event = uiState.event

    if (event == null)
        LoadingScreen()
    else
        Scaffold(
            topBar = {
                TopAppBar(title = "Edit event", onDismiss = onDismiss)
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription =null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
                EventDetails(
                    name = event.name,
                    privacy = event.privacy,
                    startTimeSeconds = event.startDate,
                    onEventDetailsClick = {}
                )
                DividerM3()
                Description(
                    description = event.description,
                    onDescriptionClick = {}
                )
                DividerM3()
                CategorySection(
                    category = "",
                    onClick = {},
                )
                DividerM3()
                LocationSection("", navigateToChooseOnMap = {})
                ShareButton(onClick = onSave, text = "Share")
            }
        }
}