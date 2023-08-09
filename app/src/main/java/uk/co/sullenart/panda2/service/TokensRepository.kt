package uk.co.sullenart.panda2.service

class TokensRepository(
    ) {
    suspend fun getAccess(): String? = "blah"

    suspend fun getRefresh(): String? = "blah"

    suspend fun store(tokens: Tokens) {
    }
}