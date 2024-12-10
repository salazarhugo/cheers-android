package com.salazar.cheers.ui.main.party.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.SearchSuggestion
import com.salazar.cheers.ui.main.party.create.recap.CreatePartyRecapScreen
import kotlinx.coroutines.launch


@Composable
fun CreatePartyScreen(
    uiState: CreatePartyUiState,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
    onPrivacyChange: (Privacy) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
    onShowGuestListToggle: () -> Unit,
) {
    val pagerState = rememberPagerState {
        4
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
//                HorizontalPagerIndicator(
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(16.dp),
//                    pageCount = pagerState.pageCount,
//                    pagerState = pagerState,
//                    activeColor = MaterialTheme.colorScheme.primary,
//                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Tabs(
                uiState = uiState,
                modifier = Modifier.weight(1f),
                pagerState = pagerState,
                onCreatePartyUIAction = onCreatePartyUIAction,
                onNameChange = onNameChange,
                onDescriptionChange = onDescriptionChange,
                onStartTimeSecondsChange = onStartTimeSecondsChange,
                onEndTimeSecondsChange = onEndTimeSecondsChange,
                onQueryChange = onQueryChange,
                onLocationClick = onLocationClick,
                onShowGuestListToggle = onShowGuestListToggle,
                onPrivacyClick = {
                }
            )
        }
    }
}

@Composable
fun Tabs(
    uiState: CreatePartyUiState,
    pagerState: PagerState,
    modifier: Modifier,
    onCreatePartyUIAction: (CreatePartyUIAction) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeSecondsChange: (Long) -> Unit,
    onEndTimeSecondsChange: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
    onShowGuestListToggle: () -> Unit,
    onPrivacyClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { page ->
        Column(modifier = Modifier.fillMaxHeight()) {
            when (page) {
                0 -> CreatePartyRecapScreen(
                    uiState = uiState,
                    onCreatePartyUIAction = {
                        when (it) {
                            CreatePartyUIAction.OnPartyDetailsClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }

                            CreatePartyUIAction.OnDescriptionClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }

                            CreatePartyUIAction.OnLocationClick ->
                                scope.launch {
                                    pagerState.animateScrollToPage(3)
                                }

                            else -> {}
                        }
                        onCreatePartyUIAction(it)
                    },
                )
            }
        }
    }
}