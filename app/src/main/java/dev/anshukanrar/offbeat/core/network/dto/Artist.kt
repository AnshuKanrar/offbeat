package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String
)