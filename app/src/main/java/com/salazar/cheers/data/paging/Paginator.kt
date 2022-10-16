package com.salazar.cheers.data.paging

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}