package domain

import com.example.core.domain.DataError
import com.example.core.domain.Result
import data.WavuWikiDataSource
import domain.model.Move

internal class FetchMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun execute(char: String): Result<List<Move>, DataError.Remote> {
        return source.fetchMoveList(char)
    }
}