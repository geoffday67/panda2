package uk.co.sullenart.panda2.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import timber.log.Timber

class Auth(
    private val tokensRepository: TokensRepository,
) {
    private interface Service {
        @POST("/token")
        @FormUrlEncoded
        suspend fun exchange(
            @Field("code") code: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("grant_type") grantType: String,
            @Field("redirect_uri") redirectUri: String,
        ): TokensResponse

        @POST("/token")
        @FormUrlEncoded
        suspend fun refresh(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("refresh_token") refreshToken: String,
            @Field("grant_type") grantType: String = "refresh_token",
        ): TokensResponse
    }

    private val service: Service by lazy {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(Service::class.java)
    }

    fun hasTokens(): Flow<Boolean> =
        tokensRepository.exists()

    suspend fun clearTokens() {
        tokensRepository.clear()
    }

    private suspend fun storeTokens(tokens: Tokens) {
        tokensRepository.store(tokens)
    }

    suspend fun exchangeCode(code: String) {
        try {
            val response = service.exchange(
                code = code,
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET,
                grantType = GRANT_TYPE,
                redirectUri = REDIRECT_URI,
            )
            val tokens = Tokens(
                accessToken = response.access_token.orEmpty(),
                refreshToken = response.refresh_token.orEmpty(),
            )
            Timber.i("Code exchanged for $tokens")
            storeTokens(tokens)
        } catch (e: Exception) {
            Timber.e("Code exchange failed")
            throw e
        }
    }

    suspend fun refresh() {
        val refreshToken = tokensRepository.getRefresh()
        try {
            val response = service.refresh(
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET,
                refreshToken = refreshToken,
            )
            val tokens = Tokens(
                accessToken = response.access_token.orEmpty(),
                refreshToken = response.refresh_token.orEmpty(),
            )
            Timber.i("Tokens refreshed $tokens")
            storeTokens(tokens)
        } catch (ignore: Exception) {
            Timber.e("Refresh failed")
        }
    }

    companion object {
        const val CLIENT_ID = "184234012755-ss36af64jltfmh91oe2fe92a3lngkdau.apps.googleusercontent.com"
        private const val CLIENT_SECRET = "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"
        private const val GRANT_TYPE = "authorization_code"
        private const val REDIRECT_URI = "https://www.sullenart.co.uk/panda/auth"
    }
}