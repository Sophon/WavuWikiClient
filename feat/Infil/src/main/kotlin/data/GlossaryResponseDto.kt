package data

import domain.Item
import kotlinx.serialization.Serializable

@Serializable
data class GlossaryResponseDto(
    val data: List<Item>,
)
