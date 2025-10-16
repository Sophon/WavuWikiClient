package data.local

import com.example.core.domain.Result
import domain.WavuError
import domain.model.Character
import domain.model.Move

class InMemoryMoveListDB: MoveListDB {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchMoveListFor(character: Character): Map<String, Move> {
        return database[character.name] ?: mapOf()
    }

    override suspend fun fetchMoveDataFor(
        character: Character,
        moveQuery: String
    ): Result<Move, WavuError> {
        val moveList = database[character.name]
            ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
        val moveData = moveList[moveQuery]
            ?: return Result.Error(WavuError.UNKNOWN_MOVE)

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(character: Character, moveList: Map<String, Move>) {
        database.put(key = character.name, value = moveList)
        character.alias.forEach { alias ->
            database[alias] = moveList
        }
    }
}