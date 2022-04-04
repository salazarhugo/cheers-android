package com.salazar.cheers.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.internal.EventUi
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val POST_STARTING_PAGE_INDEX = 0

class EventsPagingSource(
    private val service: Neo4jService,
) : PagingSource<Int, EventUi>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventUi> {
        val position = params.key ?: POST_STARTING_PAGE_INDEX
        return LoadResult.Error(throw Exception())
    }

    override fun getRefreshKey(state: PagingState<Int, EventUi>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun prettyDate(str: String): String {
        val now = ZonedDateTime.now()
        val date = ZonedDateTime.parse(str)
        val diff = ChronoUnit.SECONDS.between(date, now)

        return if (diff < 60)
            "${diff}s"
        else if (diff < 60 * 60)
            "${diff / 60}m"
        else if (diff < 60 * 60 * 24)
            "${diff / 60 / 60}h"
        else if (diff < 60 * 60 * 24 * 7)
            "${diff / 60 / 60 / 24}d"
        else if (diff < 60 * 60 * 24 * 30)
            "${diff / 60 / 60 / 24 / 7}w"
        else if (diff < 60 * 60 * 24 * 30 * 12)
            "${diff / 60 / 60 / 24 / 30}M"
        else
            "+"
    }

}