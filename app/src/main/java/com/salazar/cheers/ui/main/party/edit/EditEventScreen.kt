package com.salazar.cheers.ui.main.party.edit

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
import com.salazar.cheers.core.ui.ShareButton
import com.salazar.cheers.feature.parties.ui.PartyDetails
import androidx.compose.material3.HorizontalDivider
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.ui.main.party.create.CategorySection
import com.salazar.cheers.ui.main.party.create.Description
import com.salazar.cheers.ui.main.party.create.TopAppBar

@Composable
fun EditEventScreen(
    uiState: EditEventUiState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val event = uiState.party

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
                    model = event.bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
                PartyDetails(
                    name = event.name,
                    privacy = event.privacy,
                    startTimeSeconds = event.startDate,
                    onPartyDetailsClick = {}
                )
                HorizontalDivider()
                Description(
                    description = event.description,
                    onDescriptionClick = {}
                )
                HorizontalDivider()
                CategorySection(
                    category = "",
                    onClick = {},
                )
                HorizontalDivider()
                ShareButton(
                    onClick = onSave,
                    text = "Share",
                    isLoading = uiState.isLoading,
                )
            }
        }
}
