package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumList(
   val album : List<AlbumDto> = emptyList()
)
