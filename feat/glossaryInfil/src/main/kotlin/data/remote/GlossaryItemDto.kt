package data.remote

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItemDto(
    val term: String,
    val def: String,
    val altterm: List<String> = listOf(),
    val videos: List<String> = listOf(), //TODO: refactor
    val games: List<String> = listOf(),
    val jp: String? = null,
)
