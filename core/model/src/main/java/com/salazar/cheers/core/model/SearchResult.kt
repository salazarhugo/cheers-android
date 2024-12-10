package com.salazar.cheers.core.model

data class SearchResult(
    val parties: List<Party>,
    val users: List<UserItem>,
)