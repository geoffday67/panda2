package uk.co.sullenart.panda2.service

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val auth: Auth,
    private val tokensRepository: TokensRepository,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var accessToken = runBlocking {
            tokensRepository.getAccess()
        }

        var requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Authorization", "Bearer $accessToken")

        // Try the call once.
        var response = chain.proceed(requestBuilder.build())

        // If it failed then refresh the token and try again.
        if (response.code == 401) {
            response.close()
            requestBuilder = chain.request().newBuilder()

            // We're already in a background thread so safe (and recommended) to do a simple block here.
            runBlocking {
                auth.refresh()
                accessToken = tokensRepository.getAccess()
                requestBuilder.addHeader("Authorization", "Bearer $accessToken ")
            }
            response = chain.proceed(requestBuilder.build())
        }

        return response
    }
}
