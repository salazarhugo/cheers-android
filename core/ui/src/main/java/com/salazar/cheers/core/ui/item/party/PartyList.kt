package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.PartyID
import com.salazar.cheers.core.model.WorkerState
import com.salazar.cheers.core.ui.EmptyPartyFeed
import com.salazar.cheers.core.ui.components.worker.WorkerProgressComponent
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.shared.data.mapper.toWorkerState

@Composable
fun PartyList(
    isLoading: Boolean,
    isLoadingMore: Boolean,
    parties: List<Party>?,
    myParties: List<Party>?,
    onPartyClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onChangeCityClick: () -> Unit,
    onCreatePartyClick: () -> Unit,
    onMyPartiesClick: () -> Unit,
    onLoadMore: (Int) -> Unit,
    emptyScreen: @Composable () -> Unit = {
        EmptyPartyFeed(onClick = onChangeCityClick)
    },
) {
    val context = LocalContext.current
    val workName = Constants.PARTY_UNIQUE_WORKER_NAME
    val workManager = WorkManager.getInstance(context)
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData(workName)
        .observeAsState()
        .value
    val uploadInfo = workInfos?.firstOrNull()
    val state = uploadInfo?.state

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (state != null && state.isFinished.not()) {
            uploadingSection(
                workerState = state.toWorkerState(),
                onCancelClick = { workManager.cancelUniqueWork(workName) }
            )
        }

        myParties(
            onClick = onMyPartiesClick,
            parties = myParties ?: emptyList(),
            onPartyClick = onPartyClick,
        )

        emptyParties(
            isEmpty = parties?.isEmpty() == true,
            emptyScreen = emptyScreen,
        )

        if (parties != null) {
            parties(
                isLoading = isLoading,
                parties = parties,
                onPartyClick = onPartyClick,
                onMoreClick = onMoreClick,
                onLoadMore = onLoadMore,
            )
            if (isLoadingMore) {
                item("LoadingMoreIndicator") {
                    PlpLoadingIndicatorComponent(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

private fun LazyListScope.parties(
    isLoading: Boolean,
    parties: List<Party>,
    onPartyClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onLoadMore: (Int) -> Unit,
) {
//    if (isLoading) {
//        item {
//            PartyItemListLoading(
//                modifier = Modifier.animateItem(),
//            )
//        }
//    } else {
    items(
        count = parties.size,
    ) { index ->
        val party = parties[index]

        if (index >= parties.lastIndex) {
            LaunchedEffect(Unit) {
                onLoadMore(parties.lastIndex)
            }
        }
        PartyItem(
            party = party,
            modifier = Modifier
                .animateItem()
                .fillParentMaxWidth(1f),
            onClick = onPartyClick,
            onMoreClick = onMoreClick,
        )
    }
//    }
}

fun LazyListScope.uploadingSection(
    workerState: WorkerState,
    onCancelClick: () -> Unit,
) {
    item(key = "uploading") {
        WorkerProgressComponent(
            workerState = workerState,
            modifier = Modifier.animateItem(),
            onCancelClick = onCancelClick,
        )
    }
}

private fun LazyListScope.myParties(
    parties: List<Party>,
    onClick: () -> Unit,
    onPartyClick: (PartyID) -> Unit,
) {
    if (parties.isEmpty()) return

    item {
        val lazyListState = rememberLazyListState()
        val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

        Row(
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My parties",
                style = MaterialTheme.typography.headlineMedium,
            )
            TextButton(
                modifier = Modifier,
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "See all",
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            state = lazyListState,
            flingBehavior = snapBehavior,
        ) {
            parties(
                isLoading = false,
                parties = parties,
                onLoadMore = {},
                onPartyClick = onPartyClick,
                onMoreClick = {},
            )
        }
    }
}

private fun LazyListScope.emptyParties(
    isEmpty: Boolean,
    emptyScreen: @Composable () -> Unit,
) {
    if (!isEmpty) return
    item {
        emptyScreen()
    }
}
