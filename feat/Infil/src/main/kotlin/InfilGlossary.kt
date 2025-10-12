import com.example.core.domain.Result
import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import com.example.core.util.removeWhiteSpace
import domain.GetGlossaryUseCase
import domain.GlossaryError
import domain.GlossaryItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface InfilGlossary {
    suspend fun subscribe(): Flow<String>
    suspend fun fetchGlossary()
    fun search(query: String): Result<List<GlossaryItem>, GlossaryError>
}

internal class InfilGlossaryImpl(
    private val getGlossaryUseCase: GetGlossaryUseCase,
): InfilGlossary {
    private val outputStream = MutableStateFlow("")
    private var glossary: Map<String, GlossaryItem> = emptyMap()

    override suspend fun subscribe(): Flow<String> {
        val string = "Starting Infil Glossary"
        Napier.d(tag = TAG, message = string)

        return outputStream.apply {
            emit(string)
        }
    }

    override suspend fun fetchGlossary() {
        Napier.d(tag = TAG) { "Fetching glossary" }

        getGlossaryUseCase.execute()
            .onSuccess { items ->
                glossary = buildMap {
                    items.forEach { item ->
                        put(item.term, item) //use main term
                        item.altTerm.forEach { alt -> //alt term
                            put(alt, item)
                        }
                    }
                }
                Napier.d(tag = TAG) { "Successfully retrieved glossary; ${items.size} keys" }
            }
            .onError { errorType ->
                Napier.e(tag = TAG) { "Error: $errorType" }
                outputStream.emit("Error: $errorType")
            }
    }

    override fun search(query: String): Result<List<GlossaryItem>, GlossaryError> {
        if (glossary.isEmpty()) return Result.Error(GlossaryError.EMPTY_GLOSSARY)

        Napier.d(tag = TAG) { "Query: $query" }

        val result = glossary.entries
            .filter {
                it.key.contains(query, ignoreCase = true)
                        || it.key.contains(query.removeWhiteSpace(), ignoreCase = true)
            }
            .map { it.value }

        return Result.Success(result)
    }
}


private const val TAG = "InfilGlossary"