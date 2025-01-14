package com.salazar.cheers.feature.parties.myparties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.domain.feed_party.ListPartyFlowUseCase
import com.salazar.cheers.domain.feed_party.ListPartyUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyPartiesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val query: String = "",
    val parties: List<Party>? = null,
    val filters: List<Filter> = emptyList(),
)

@HiltViewModel
class MyPartiesViewModel @Inject constructor(
    private val listMyPartyFlowUseCase: ListPartyFlowUseCase,
    private val listPartyUseCase: ListPartyUseCase,
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MyPartiesUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listParty()
        }
        viewModelScope.launch {
            listMyPartyFlowUseCase()
                .collect(::updateMyParties)
        }
    }

    fun onSwipeToRefresh() {
        viewModelScope.launch {
            listParty()
        }
    }

    private suspend fun listParty(
        page: Int = 1,
    ) {
        updateIsLoading(true)
        val filters = viewModelState.value.filters

        val result = listPartyUseCase(
            filters = filters,
            page = page,
        ).getOrNull() ?: return

        updateFilters(result.second)
        updateIsLoading(false)
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message, isLoading = false)
        }
    }

    private fun updateFilters(filters: List<Filter>) {
        viewModelState.update {
            it.copy(filters = filters)
        }
    }

    private fun updateMyParties(parties: List<Party>?) {
        viewModelState.update {
            it.copy(parties = parties, isLoading = false)
        }
    }

    fun onLoadMore(lastLoadedIndex: Int) {
        val nextItemIndex = lastLoadedIndex + 1
        val nextPage = nextItemIndex / 10 + 1
        viewModelState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            listParty(page = nextPage)
            viewModelState.update { it.copy(isLoadingMore = false) }
        }
    }

    fun onFilterClick(filter: Filter) {
        viewModelState.update {
            it.copy(
                filters = it.filters.map { existingFilter ->
                    if (existingFilter.id == filter.id) {
                        existingFilter.copy(selected = true)
                    } else {
                        existingFilter.copy(selected = false)
                    }
                }
            )
        }
        viewModelScope.launch {
            partyRepository.updateFilter(filter.id)
        }
    }
}
