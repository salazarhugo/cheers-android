package com.salazar.cheers.core.util.paging

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}