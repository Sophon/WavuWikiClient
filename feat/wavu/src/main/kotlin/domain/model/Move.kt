package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val id: String,
    val name: String,
    val input: String,
    val parent: String,
    val target: String,
    val damage: String,
    val startup: String,
    val recv: String? = null,
    val tot: String? = null,
    val crush: String? = null,
    val block: String? = null,
    val hit: String? = null,
    val ch: String? = null,
    val notes: String? = null,
    val alias: String? = null,
    val image: String? = null,
    val video: String? = null,
    val alt: String? = null,
    val ns: String,
)