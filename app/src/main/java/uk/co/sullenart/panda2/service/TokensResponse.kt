package uk.co.sullenart.panda2.service

import kotlinx.serialization.Serializable

@Serializable
data class TokensResponse(
    val access_token: String? = null,
    val refresh_token: String? = null,
)
