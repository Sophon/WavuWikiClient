package domain

import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import data.WavuWikiDataSource
import domain.model.Move

internal class FetchMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun execute(char: String): Result<Map<String, Move>, DataError.Remote> {
        return source.fetchMoveList(char)
            .map { dto -> dto.cargoQuery.map { it.title } }
            .map { moves ->
                moves.associateBy { move ->
                    move.id.substringAfter("-")
                }
            }
    }
}