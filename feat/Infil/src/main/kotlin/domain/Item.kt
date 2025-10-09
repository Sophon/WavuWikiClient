package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val term: String,
    @SerialName("def") val definition: String,
    val videos: List<String>, //TODO: refactor
    val games: List<String>,
    @SerialName("jp") val jpTranslation: String,
)