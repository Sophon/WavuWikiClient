package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val id: String,
    val input: String,
    val target: String, //TODO: hml sl sm etc
    val name: String? = null,
    val parent: String? = null,
    val damage: String? = null,
    val startup: String? = null,
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
    val ns: String? = null, //TODO: what is this
)