package uk.co.sullenart.panda2.photos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import uk.co.sullenart.panda2.service.GooglePhotos
import java.io.IOException

class PhotoPager(
    private val googlePhotos: GooglePhotos,
) {
    val flowPhotos: Flow<PagingData<Photo>> get() = pager.flow

    private val pager = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = true,
            prefetchDistance = PAGE_SIZE * 2,
        ),
        initialKey = 0,
    ) {
        PhotoPageSource()
    }

    companion object {
        const val PAGE_SIZE = 20
    }

    inner class PhotoPageSource : PagingSource<Int, Photo>() {
        override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
            try {
                val pageNumber = params.key ?: 0
                Timber.d("Sourcing page $pageNumber")

                val result = googlePhotos.getAlbums().map { Photo(title = it.title) }
//                val result = listOf(
//                    Photo("Geoff"),
//                    Photo("is"),
//                    Photo("great!"),
//                )

                return LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = null,
                    itemsBefore = 0,
                    itemsAfter = 0,
                )
            } catch (e: IOException) {
                return LoadResult.Error(e)
            }
        }
    }
}