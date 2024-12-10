package com.salazar.cheers.ui.main.party.create.recap

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.party.PartyBannerComponent
import com.salazar.cheers.core.ui.components.row.AddLocationItem
import com.salazar.cheers.core.ui.components.row.CoreRowItem
import com.salazar.cheers.feature.create_post.PrivacyBottomSheet
import com.salazar.cheers.feature.parties.ui.PartyDescription
import com.salazar.cheers.feature.parties.ui.PartyDetails
import com.salazar.cheers.feature.parties.ui.PartyInfo
import com.salazar.cheers.ui.main.party.create.CreatePartyUIAction
import com.salazar.cheers.ui.main.party.create.CreatePartyUiState
import kotlinx.coroutines.launch


@Composable
fun CreatePartyRecapScreen(
    uiState: CreatePartyUiState,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(),
                shadowElevation = 8.dp,
            ) {
                ShareButton(
                    modifier = Modifier.navigationBarsPadding(),
                    enabled = uiState.name.isNotBlank() && uiState.address.isNotBlank(),
                    onClick = {
                        onCreatePartyUIAction(CreatePartyUIAction.OnUploadParty)
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(it),
        ) {
            CreatePartyRecapTopBar(
                onDismiss = { onCreatePartyUIAction(CreatePartyUIAction.OnDismiss) },
                title = "New party",
            )
            AddPhoto(
                photo = uiState.photo,
                onAddPhotoClick = { onCreatePartyUIAction(CreatePartyUIAction.OnAddPhoto) },
            )
            if (uiState.name.isNotBlank()) {
                PartyDetails(
                    name = uiState.name,
                    privacy = uiState.privacy,
                    startTimeSeconds = uiState.startDateTimeMillis / 1000,
                    onPartyDetailsClick = { onCreatePartyUIAction(CreatePartyUIAction.OnPartyDetailsClick) }
                )
                PartyInfo(
                    modifier = Modifier,
                    startDate = uiState.startDateTimeMillis / 1000,
                    city = uiState.city,
                    privacy = uiState.privacy,
                    address = uiState.address,
                    onTicketingClick = {},
                    onUserClick = {},
                    onPrivacyClick = {
                        showBottomSheet = true
                    },
                    onAddressClick = {
                        onCreatePartyUIAction(CreatePartyUIAction.OnLocationClick)
                    }
                )
            }
            if (uiState.description.isNotBlank()) {
                PartyDescription(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    description = uiState.description,
                    onUserClicked = {
                        onCreatePartyUIAction(CreatePartyUIAction.OnUserClick(it))
                    },
                    onDescriptionClick = {
                        onCreatePartyUIAction(CreatePartyUIAction.OnDescriptionClick)
                    }
                )
            }
            if (uiState.name.isBlank()) {
                AddBasicInfoItem(
                    onDescriptionClick = { onCreatePartyUIAction(CreatePartyUIAction.OnBasicInfoClick) }
                )
            }
            if (uiState.description.isBlank()) {
                HorizontalDivider()
                AddDescriptionItem(
                    onDescriptionClick = { onCreatePartyUIAction(CreatePartyUIAction.OnDescriptionClick) }
                )
            }
            if (uiState.address.isBlank()) {
                HorizontalDivider()
                AddLocationItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        onCreatePartyUIAction(CreatePartyUIAction.OnLocationClick)
                    }
                )
            }
            HorizontalDivider()
        }
    }

    if (showBottomSheet) {
        PrivacyBottomSheet(
            privacy = uiState.privacy,
            privacyState = sheetState,
            onSelectPrivacy = {
                onCreatePartyUIAction(CreatePartyUIAction.OnPrivacyChange(it))
            },
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showBottomSheet = false
                }
            }
        )
    }
}


@Composable
fun Privacy(
    privacy: Privacy,
    onShowClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onShowClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(privacy.icon, null)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(privacy.title)
                Text(privacy.subtitle)
            }
        }
        Icon(Icons.Filled.KeyboardArrowRight, null)
    }
}

@Composable
fun AddPhoto(
    photo: Uri?,
    onAddPhotoClick: () -> Unit,
) {
    if (photo != null) {
        PartyBannerComponent(
            bannerUrl = photo.toString(),
            onClick = onAddPhotoClick,
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(16 / 9f)
                .background(MaterialTheme.colorScheme.outline)
                .clickable { onAddPhotoClick() },
            contentAlignment = Alignment.Center,
        ) {
            FilledTonalButton(onClick = onAddPhotoClick) {
                Icon(Icons.Outlined.PhotoCamera, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add photo")
            }
        }
    }
}

@Composable
fun AddBasicInfoItem(
    onDescriptionClick: () -> Unit = {},
) {
    CoreRowItem(
        modifier = Modifier.padding(horizontal = 16.dp),
        title = "Add basic information",
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
            )
        },
        onClick = onDescriptionClick,
        trailingIcon = {
            IconButton(
                onClick = onDescriptionClick,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
fun AddDescriptionItem(
    onDescriptionClick: () -> Unit = {},
) {
    CoreRowItem(
        title = "Add description",
        modifier = Modifier.padding(horizontal = 16.dp),
        icon = {
            Icon(Icons.Outlined.Description, "Description icon")
        },
        trailingIcon = {
            IconButton(
                onClick = onDescriptionClick,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            }
        },
        onClick = onDescriptionClick,
    )
}

@Composable
private fun ShareButton(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val text = "Create Event"
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(Modifier.width(1.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.padding(12.dp),
            enabled = enabled,
        ) {
            Text(text = text)
        }
    }
}

@ScreenPreviews
@Composable
private fun CreatePartyRecapScreenPreview() {
    CheersPreview {
        CreatePartyRecapScreen(
            uiState = CreatePartyUiState(isLoading = false),
            onCreatePartyUIAction = {

            },
        )
    }
}