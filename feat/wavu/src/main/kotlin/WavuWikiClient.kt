import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import domain.FetchMoveListUseCase
import io.github.aakira.napier.Napier

interface WavuWikiClient {
    suspend fun frameData(query: String)
}

internal class WavuWikiClientImpl(
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): WavuWikiClient {
//    private val characters: List<Character>
//
//    init {
//        characters = fetchCharacters()
//    }

    override suspend fun frameData(query: String) {
        fetchMoveListUseCase.execute(query)
            .onSuccess { moveList ->
                Napier.d(tag = TAG) { "Success; ${moveList.size} moves for char $query" }
            }
            .onError { error ->
                Napier.e(tag = TAG) { "Error: $error" }
            }
    }

    private fun fetchCharacters(): List<Character> {
        TODO("load from the config.json")
    }
}


private const val TAG = "WavuWikiClient"