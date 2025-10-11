import com.example.core.domain.Result
import domain.FetchMoveListUseCase
import domain.model.Character
import domain.model.Move
import io.github.aakira.napier.Napier

interface WavuWikiClient {
    suspend fun fetchCompleteMoveList()
    suspend fun fetchMoveListFor(character: Character): Map<String, Move>?
    fun frameDataFor(charName: String, move: String): Move?
}

internal class WavuWikiClientImpl(
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): WavuWikiClient {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchCompleteMoveList() {
        fetchCharacters().forEach { character ->
            fetchMoveListFor(character)?.let { moveList ->
                database.put(key = character.name, value = moveList)
                Napier.d(tag = TAG) { "${moveList.size} moves for ${character.name} added" }
            }
        }
    }

    override suspend fun fetchMoveListFor(character: Character): Map<String, Move>? {
        val result = fetchMoveListUseCase.execute(character.name)
        return when (result) {
            is Result.Success -> {
                result.data.also { moveList ->
                    Napier.d(tag = TAG) { "Success; ${moveList.size} moves for char $character" }
                }
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error: ${result.error}" }
                null
            }
        }
    }

    override fun frameDataFor(charName: String, move: String): Move? {
        return database[charName]?.get(move)
    }


    private fun fetchCharacters(): List<Character> {
        //TODO: load from the config.json
        return listOf(

        )
    }
}


private const val TAG = "WavuWikiClient"