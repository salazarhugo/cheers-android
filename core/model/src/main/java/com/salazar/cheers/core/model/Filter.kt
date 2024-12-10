package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Filter(
    val id: String,
    val name: String,
    val selected: Boolean,
)


const val ALL_FILTER_ID = "ALL_FILTER_ID"
const val INFO_FILTER_ID = "INFO_FILTER_ID"

val emptyFilter = Filter(
    id = String(),
    name = String(),
    selected = false,
)

val allFilter = Filter(
    id = ALL_FILTER_ID,
    name = "All",
    selected = false,
)

val infoFilter = Filter(
    id = INFO_FILTER_ID,
    name = "Infos",
    selected = false,
)

val userFilter = Filter(
    id = "USER_FILTER_ID",
    name = "User",
    selected = false,
)
