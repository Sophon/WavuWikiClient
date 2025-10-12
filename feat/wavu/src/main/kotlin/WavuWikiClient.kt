import com.example.core.domain.Result
import domain.FetchMoveListUseCase
import domain.WavuError
import domain.model.Character
import domain.model.CharacterList
import domain.model.Move
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import java.io.File

interface WavuWikiClient {
    suspend fun fetchCompleteMoveList()
    fun frameDataFor(charName: String, move: String): Result<Move, WavuError>
}

internal class WavuWikiClientImpl(
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): WavuWikiClient {
    val json = Json {
        ignoreUnknownKeys = true
    }
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchCompleteMoveList() {
        fetchCharacters().forEach { character ->
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
    }

    override fun frameDataFor(charName: String, move: String): Result<Move, WavuError> {
        val moveList = database[charName] ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
        val moveData = moveList[move] ?: return Result.Error(WavuError.UNKNOWN_MOVE)
        return Result.Success(moveData)
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