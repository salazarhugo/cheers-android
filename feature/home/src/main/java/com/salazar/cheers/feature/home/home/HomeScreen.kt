package com.salazar.cheers.feature.home.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.feature.home.components.HomeTopBar
import com.salazar.cheers.feature.home.friend_feed.FriendFeedScreen
import com.salazar.cheers.feature.home.note.NoteBottomSheet
import com.salazar.cheers.feature.home.party_feed.PartyFeedStateful
import com.salazar.cheers.feature.home.select_city.SelectCityBottomSheet
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    initialSelectedTab: Int,
    onHomeUIAction: (HomeUIAction) -> Unit,
    navigateToPartyDetail: (String) -> Unit = {},
    navigateToCreateParty: () -> Unit = {},
) {
    val selectedPage = uiState.selectedPage
    val state = rememberRefreshLayoutState()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = initialSelectedTab,
        initialPageOffsetFraction = 0f,
        pageCount = { HomeSelectedPage.entries.size },
    )
    val collapsedFraction by remember {
        derivedStateOf {
            val firstVisibleIndex = uiState.listState.firstVisibleItemIndex
            firstVisibleIndex.toFloat().coerceIn(0f..1f)
        }
    }
    var showSelectCitySheet by remember { mutableStateOf(false) }
    var showNoteSheet: UserID? = remember { mutableStateOf(null) }.value
    val citySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val noteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(pagerState.currentPage) {
        onHomeUIAction(HomeUIAction.OnSelectPage(pagerState.currentPage))
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                currentCity = uiState.currentCity,
                pagerState = pagerState,
                homeSelectedPage = selectedPage,
                collapsedFraction = collapsedFraction,
                notificationCount = uiState.notificationCount,
                onActivityClick = { onHomeUIAction(HomeUIAction.OnActivityClick) },
                onCityClick = {
                    showSelectCitySheet = true
                },
                onMapClick = { onHomeUIAction(HomeUIAction.OnMapClick) },
                onCameraClick = { onHomeUIAction(HomeUIAction.OnCameraClick) },
                onMyPartiesClick = { onHomeUIAction(HomeUIAction.OnMyPartiesClick) },
            )
        },
        floatingActionButton = {
            if (!uiState.showFloatingActionButton)
                return@Scaffold

            FloatingActionButton(
                onClick = { onHomeUIAction(HomeUIAction.OnCreatePostClick) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Floating action button.",
                )
            }
        },
    ) {
        LaunchedEffect(uiState.isLoading) {
            if (!uiState.isLoading) {
                scope.launch {
                    state.finishRefresh(true, delay = 0L)
                }
            }
        }

        HomeScreenContent(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            pagerState = pagerState,
            isRefreshing = false,
            friendsTabContent = {
                FriendFeedScreen(
                    uiState = uiState,
                    onHomeUIAction = {
                        when (it) {
                            is HomeUIAction.OnNoteClick -> {
                                showNoteSheet = it.userID
                            }

                            else -> onHomeUIAction(it)
                        }
                    },
                )
            },
            partiesTabContent = {
                PartyFeedStateful(
                    navigateToPartyMoreSheet = {},
                    navigateToPartyDetail = navigateToPartyDetail,
                    navigateToCreateParty = navigateToCreateParty,
                    onChangeCityClick = {
                        showSelectCitySheet = true
                    },
                    navigateToMyParties = {
                        onHomeUIAction(HomeUIAction.OnMyPartiesClick)
                    },
                )
            },
        )
    }

    if (showNoteSheet != null) {
        NoteBottomSheet(
            userID = showNoteSheet!!,
            sheetState = noteSheetState,
            onDismiss = {
                scope.launch {
                    noteSheetState.hide()
                }.invokeOnCompletion {
                    showNoteSheet = null
                }
            },
            modifier = Modifier,
            navigateToCreateNote = { onHomeUIAction(HomeUIAction.OnCreateNoteClick) },
        )
    }
    if (showSelectCitySheet) {
        SelectCityBottomSheet(
            onDismiss = {
                scope.launch {
                    citySheetState.hide()
                }.invokeOnCompletion {
                    showSelectCitySheet = false
                }
            },
        )
    }
}

@Composable
fun HomeLazyPagingListState(
    lazyPagingItems: LazyPagingItems<Post>,
) {
    lazyPagingItems.apply {
        when {
            loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount < 1 -> {
//                NoPosts()
            }

            loadState.refresh is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }

            loadState.append is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            loadState.append is LoadState.Error -> {
                val e = lazyPagingItems.loadState.append as LoadState.Error
                Text(
                    text = e.error.localizedMessage!!,
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun HomeScreenPreview() {
    CheersPreview {
        HomeScreen(
            uiState = HomeUiState(),
            initialSelectedTab = 0,
            onHomeUIAction = {},
        )
    }
}
