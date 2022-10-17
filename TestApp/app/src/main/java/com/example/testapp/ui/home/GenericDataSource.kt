package com.example.testapp.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.reflect.KSuspendFunction5

class GenericDataSource<T : Any>(
    private val pagerFun: KSuspendFunction5<Int, Int, String?, String?, String?, List<T>>,
    private val search: String? = null
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(20) ?: page.nextKey?.minus(20)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 0
        val pageSize = params.loadSize.coerceAtMost(30)

        return try {
            val response =
                pagerFun(
                    page,
                    pageSize,
                    if (!search.isNullOrEmpty()) search else null,
                    null,
                    null
                )

            LoadResult.Page(
                data = response,
                prevKey = if (page == 0) null else page - 20,
                nextKey = if (response.size < pageSize) null else page + 20
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}