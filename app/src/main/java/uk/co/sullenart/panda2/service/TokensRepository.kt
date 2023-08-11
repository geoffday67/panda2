package uk.co.sullenart.panda2.service

import kotlinx.coroutines.flow.Flow
import uk.co.sullenart.panda2.Settings

class TokensRepository(
    private val settings: Settings,
) {
    fun exists(): Flow<Boolean> =
        settings.exists(SETTINGS_KEY)

    suspend fun clear() {
        settings.remove(SETTINGS_KEY)
    }
    suspend fun getAccess(): String {
        val tokens = settings.fetch<Tokens>(SETTINGS_KEY)
        return tokens?.accessToken.orEmpty()
    }

    suspend fun getRefresh(): String {
        val tokens = settings.fetch<Tokens>(SETTINGS_KEY)
        return tokens?.refreshToken.orEmpty()
    }

    suspend fun store(tokens: Tokens) {
        settings.store(SETTINGS_KEY, tokens)
    }

    companion object {
        private const val SETTINGS_KEY = "tokens"
    }
}