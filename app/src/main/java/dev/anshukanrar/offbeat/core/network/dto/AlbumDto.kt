package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val artistId: String = "",
    val artists: List<Artist> = emptyList(),
    val songCount: Int = 0,
    val duration: Int = 0,
    val created: String = "",
    val year: Int? = null,
    val coverArt: String? = null,
)