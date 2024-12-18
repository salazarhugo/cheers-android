package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.salazar.cheers.core.model.Party
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
    onPartyClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onChangeCityClick: () -> Unit,
    onCreatePartyClick: () -> Unit,
    onLoadMore: (Int) -> Unit,
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

        createParty(
            onClick = onCreatePartyClick,
        )

        emptyParties(
            isEmpty = parties?.isEmpty() == true,
            onChangeCityClick = onChangeCityClick,
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
    if (isLoading) {
        item {
            PartyItemListLoading(
                modifier = Modifier.animateItem(),
            )
        }
    } else {
        items(
            count = parties.size,
        ) { index ->
            val party = parties[index]

            if (index >= parties.size - 1) {
                onLoadMore(index)
            }
            PartyItem(
                party = party,
                modifier = Modifier.animateItem(),
                onClick = onPartyClick,
                onMoreClick = onMoreClick,
            )
        }
    }
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

private fun LazyListScope.createParty(
    onClick: () -> Unit,
) {
    item {
        Button(
            modifier = Modifier
                .animateItem()
                .padding(horizontal = 16.dp),
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Create Party")
        }
    }
}

private fun LazyListScope.emptyParties(
    isEmpty: Boolean,
    onChangeCityClick: () -> Unit,
) {
    if (!isEmpty)
        return
    item {
        EmptyPartyFeed(
            onClick = onChangeCityClick,
        )
    }
}
