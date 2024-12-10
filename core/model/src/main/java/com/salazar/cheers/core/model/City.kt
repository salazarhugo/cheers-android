package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class City(
    val id: String,
    val name: String,
)

val emptyCity = City(
    id = String(),
    name = String(),
)

val parisCity = City(
    id = "PARIS_CITY_ID",
    name = "Paris",
)

val londonCity = City(
    id = "LONDON_CITY_ID",
    name = "London",
)
