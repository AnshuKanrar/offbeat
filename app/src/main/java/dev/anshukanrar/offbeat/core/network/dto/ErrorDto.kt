package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: Int = 0,
    val message: String = "",
)