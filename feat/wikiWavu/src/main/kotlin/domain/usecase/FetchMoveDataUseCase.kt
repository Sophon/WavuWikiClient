package domain.usecase

import com.example.core.domain.Result
import data.local.MoveListDB
import domain.WavuError
import domain.model.Move

class FetchMoveDataUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        return db.fetchMoveDataFor(charName, moveQuery)
    }
}