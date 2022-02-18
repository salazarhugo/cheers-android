package com.salazar.cheers.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.PostRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.Post

private const val POST_STARTING_PAGE_INDEX = 0

class PostsPagingSource(
    private val service: Neo4jService,
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val position = params.key ?: POST_STARTING_PAGE_INDEX
        val response = service.posts(position, params.loadSize)

        return when (response) {
            is Result.Success -> {
//                val posts = response.data.map {
//                    it.copy(createdTime = prettyDate(it.createdTime))
//                }

                LoadResult.Page(
                    data = listOf(),
                    prevKey = if (position == POST_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (true) null else position + (params.loadSize / NETWORK_PAGE_SIZE)
                )
            }
            is Result.Error -> LoadResult.Error(response.exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}