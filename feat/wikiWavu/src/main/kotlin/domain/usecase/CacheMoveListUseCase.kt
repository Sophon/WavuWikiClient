package domain.usecase

import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import data.local.MoveListDB
import domain.WavuError
import domain.model.Character
import domain.model.Move

class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        character: Character,
        moveList: Map<String, Move>
    ): EmptyResult<WavuError> {
        db.insertMoveList(
            charName = character.name.lowercase(),
            moveList = moveList
        )
        character.alias.forEach { alias ->
            db.insertMoveList(charName = alias, moveList = moveList)
        }
        return Result.Success(Unit)
    }
}