package com.salazar.cheers.core.model


sealed interface RecentSearch {
    data class Text(
        val text: String,
    ) : RecentSearch

    data class Party(
        val party: com.salazar.cheers.core.model.Party,
    ) : RecentSearch

    data class User(
        val user: UserItem,
    ) : RecentSearch
}

fun RecentSearch.toUserItem(): UserItem {
    return when (this) {
        is RecentSearch.Party -> emptyUserItem.copy(name = this.party.name)
        is RecentSearch.Text -> emptyUserItem.copy(name = this.text)
        is RecentSearch.User -> user
    }
}