package com.salazar.cheers.shared.data.mapper

import cheers.search.v1.SearchResponse
import com.salazar.cheers.core.model.SearchResult


fun SearchResponse.toSearchResult(): SearchResult {
    return SearchResult(
        users = usersList.map { it.toUserItem() },
        parties = eventsList.map { it.toParty() },
    )
}
