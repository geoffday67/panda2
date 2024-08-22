package uk.co.sullenart.panda2.photos

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val title: String,
    val coverUrl: String,
    val items: Int,
)
