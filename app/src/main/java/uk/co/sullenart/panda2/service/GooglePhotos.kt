package uk.co.sullenart.panda2.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import uk.co.sullenart.panda2.photos.Album

class GooglePhotos(
    private val authInterceptor: AuthInterceptor,
) {
    @Serializable
    private data class AlbumsResponse(
        val albums: List<AlbumResponse>?,
    ) {
        @Serializable
        data class AlbumResponse(
            val title: String?,
            val coverPhotoBaseUrl: String?,
            val mediaItemsCount: Int?,
        )
    }

    private interface Service {
        @GET("/v1/albums")
        suspend fun albums(): AlbumsResponse
    }

    private val service: Service by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://photoslibrary.googleapis.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(Service::class.java)
    }

    suspend fun getAlbums(): List<Album> =
        try {
            service.albums().albums.orEmpty().map {
                Album(
                    title = it.title.orEmpty(),
                    items = it.mediaItemsCount ?: 0,
                    coverUrl = "${it.coverPhotoBaseUrl}=w2048-h1024"
                )
            }
        } catch (ignore: Exception) {
            emptyList()
        }
}