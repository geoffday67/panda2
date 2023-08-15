package uk.co.sullenart.panda2.photos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import uk.co.sullenart.panda2.service.GooglePhotos
import java.io.IOException

class AlbumPager(
    private val googlePhotos: GooglePhotos,
) {
    val flowAlbums: Flow<PagingData<Album>> get() = pager.flow

    private val pager = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = true,
            prefetchDistance = PAGE_SIZE * 2,
        ),
        initialKey = 0,
    ) {
        AlbumPageSource()
    }

    companion object {
        const val PAGE_SIZE = 20
    }

    inner class AlbumPageSource : PagingSource<Int, Album>() {
        override fun getRefreshKey(state: PagingState<Int, Album>): Int = 0

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
            return try {
                val result = googlePhotos.getAlbums()

                LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = null,
                    itemsBefore = 0,
                    itemsAfter = 0,
                )
            } catch (e: IOException) {
                LoadResult.Error(e)
            }
        }
    }
}