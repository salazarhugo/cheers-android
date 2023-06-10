package com.salazar.cheers.core.model

fun com.mapbox.search.result.SearchSuggestion.toSearchSuggestion(): SearchSuggestion {
    return SearchSuggestion(
        name = name,
        icon = makiIcon,
        address = address?.formattedAddress(),
    )
}