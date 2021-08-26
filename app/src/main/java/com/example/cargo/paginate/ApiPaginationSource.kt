package com.example.cargo.paginate

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cargo.api.ApiInterface
import com.example.cargo.data.Photo
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class ApiPaginationSource constructor(private val api: ApiInterface) : PagingSource<Int, Photo>() {
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val pageIndex = params.key ?: STARTING_PAGE_INDEX
            val response = api.galleryApi(page = pageIndex)
            val nextKey = if (response.stat.isEmpty() || response.stat.isBlank())
                null
            else
                pageIndex + 1

            LoadResult.Page(
                data = response.photos.photo,
                prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}