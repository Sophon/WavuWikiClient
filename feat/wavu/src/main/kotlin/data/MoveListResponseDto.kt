package data

import domain.model.Move
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveListResponseDto(
    @SerialName("cargoquery") val cargoQuery: List<Title>,
) {
    @Serializable
    data class Title(
        @SerialName("title") val title: Move
    )
}
