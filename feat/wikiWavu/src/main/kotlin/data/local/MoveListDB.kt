package data.local

import com.example.core.domain.Result
import domain.WavuError
import domain.model.Character
import domain.model.Move

//TODO: use Result -> think about Errors
interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Map<String, Move>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun insertMoveList(character: Character, moveList: Map<String, Move>)
}