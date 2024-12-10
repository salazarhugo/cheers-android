package com.salazar.cheers.feature.home.home

enum class HomeSelectedPage(val page: String) {
    FRIENDS("Friends"),
    PARTIES("Parties");

    companion object {
        fun getByPage(page: Int) = entries.getOrNull(page) ?: FRIENDS
    }
}
