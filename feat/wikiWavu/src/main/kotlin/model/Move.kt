package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://wavu.wiki/t/Template:Move">Wavu Wiki Move Template</a>
 */
@Serializable
data class Move(
    val id: String,
    val input: String,
    @SerialName("target") val level: String? = null,
    val name: String? = null,
    val parent: String? = null,
    val damage: String? = null,
    val startup: String? = null,
    @SerialName("recv") val recovery: String? = null,
    @SerialName("tot") val totalFrames: String? = null,
    val crush: String? = null,
    val block: String? = null,
    val hit: String? = null,
    val ch: String? = null,
    val notes: String? = null,
    val alias: String? = null,
    val image: String? = null,
    val video: String? = null,
    val alt: String? = null,
) {
    val characterName get() = id.substringBefore('-')
}