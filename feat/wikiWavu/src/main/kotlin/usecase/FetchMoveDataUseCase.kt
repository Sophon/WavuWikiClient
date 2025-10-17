package usecase

import com.example.core.domain.Result
import dataLocal.MoveListDB
import WavuError
import model.Move

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