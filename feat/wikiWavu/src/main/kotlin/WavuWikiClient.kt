import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.domain.Service
import com.example.core.domain.Source
import domain.WavuError
import domain.model.Character
import domain.model.CharacterList
import domain.model.Move
import domain.usecase.CacheMoveListUseCase
import domain.usecase.FetchCharacterListUseCase
import domain.usecase.DownloadMoveListUseCase
import domain.usecase.FetchMoveDataUseCase
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import java.io.File

interface WavuWikiClient: Service {
    suspend fun fetchCompleteMoveList(): EmptyResult<WavuError>
    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
}

internal class WavuWikiClientImpl(
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
    private val json: Json,
): WavuWikiClient {
//    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchCompleteMoveList(): EmptyResult<WavuError> {
        return when (val result = fetchCharacterListUseCase.invoke()) {
            is Result.Success -> {
                result.data.characterList.forEach { character ->
                    fetchMoveListFor(character)?.let { moveList ->
                        cacheMoveListUseCase.invoke(character, moveList)

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

    override suspend fun frameDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        return fetchMoveDataUseCase.invoke(charName, moveQuery)
    }

    override fun source(): Source {
        return Source(
            name = SERVICE_NAME,
            iconUrl = "https://i.imgur.com/0cnTzNk.png"
        )
    }


    //TODO: should return a Result
    //TODO: usecase
    private suspend fun fetchMoveListFor(character: Character): Map<String, Move>? {
        val result = downloadMoveListUseCase.execute(character.name)
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
}


private const val TAG = "WavuWikiClient"