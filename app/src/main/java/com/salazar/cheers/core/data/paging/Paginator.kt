package com.salazar.cheers.core.data.paging

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}