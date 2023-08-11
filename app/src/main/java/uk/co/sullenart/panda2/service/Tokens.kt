package uk.co.sullenart.panda2.service

import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val accessToken: String = "",
    val refreshToken: String = "",
)
