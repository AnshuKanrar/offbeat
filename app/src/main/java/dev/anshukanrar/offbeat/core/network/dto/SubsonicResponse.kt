package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubsonicResponse(
    val status : String = "",
    val version: String = "",
    val type: String? = null,
    val serverVersion: String? = null,
    val openSubsonic : Boolean = false,
    val error: ErrorDto? = null,
    @SerialName("albumList2") val albumList: AlbumList? = null
)