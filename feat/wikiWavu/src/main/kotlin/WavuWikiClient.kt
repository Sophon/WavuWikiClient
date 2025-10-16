import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.domain.Service
import com.example.core.domain.Source
import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import domain.usecase.FetchMoveListUseCase
import domain.WavuError
import domain.model.Character
import domain.model.CharacterList
import domain.model.Move
import domain.usecase.FetchCharacterListUseCase
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import java.io.File

interface WavuWikiClient: Service {
    suspend fun fetchCompleteMoveList(): EmptyResult<WavuError>
    fun frameDataFor(charName: String, move: String): Result<Move, WavuError>
}

internal class WavuWikiClientImpl(
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
    private val json: Json,
): WavuWikiClient {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchCompleteMoveList(): EmptyResult<WavuError> {
        return when (val result = fetchCharacterListUseCase.invoke()) {
            is Result.Success -> {
                result.data.characterList.forEach { character ->
                    fetchMoveListFor(character)?.let { moveList ->
                        insertMoveListIntoDatabase(
                            character = character,
                            moveList = moveList
                        )
                        Napier.d(tag = TAG) {
                            "${moveList.size} moves for ${character.name} (${character.alias}) added"
                        }
                    }
                }

                Result.Success(Unit)
            }
            is Result.Error -> {
                Result.Error(result.error)
            }
        }
    }

    override fun frameDataFor(charName: String, move: String): Result<Move, WavuError> {
        val moveList = database[charName] ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
        val moveData = moveList[move] ?: return Result.Error(WavuError.UNKNOWN_MOVE)
        return Result.Success(moveData)
    }

    override fun source(): Source {
        return Source(
            name = SERVICE_NAME,
            iconUrl = "https://i.imgur.com/0cnTzNk.png"
        )
    }


    //TODO: should return a Result
    private suspend fun fetchMoveListFor(character: Character): Map<String, Move>? {
        val result = fetchMoveListUseCase.execute(character.name)
        return when (result) {
            is Result.Success -> {
                result.data
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error: ${result.error} for $character" }
                null
            }
        }
    }

    //TODO: ConfigRepo
    //TODO: new character.json
    private fun fetchCharacters(): List<Character> {
        val configFile = File(CONFIG_FILE)
        val charList = json.decodeFromString<CharacterList>(configFile.readText())
        return charList.characterList
    }

    private fun insertMoveListIntoDatabase(
        character: Character,
        moveList: Map<String, Move>,
    ) {
        database.put(key = character.name, value = moveList)
        character.alias.forEach { alias -> database[alias] = moveList }
    }
}


private const val TAG = "WavuWikiClient"