import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import com.example.core.util.removeWhiteSpace
import domain.GetGlossaryUseCase
import domain.GlossaryItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface InfilGlossary {
    suspend fun subscribe(): Flow<String>
    suspend fun fetchGlossary()
    fun search(query: String): List<GlossaryItem>
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
                outputStream.emit("Successfully retrieved: ${items.size}")
            }
            .onError { errorType ->
                Napier.e(tag = TAG) { "Error: $errorType" }
                outputStream.emit("Error: $errorType")
            }
    }

    override fun search(query: String): List<GlossaryItem> {
        return glossary.entries
            .filter {
                it.key.contains(query, ignoreCase = true)
                        || it.key.contains(query.removeWhiteSpace(), ignoreCase = true)
            }
            .map { it.value }
    }
}


private const val TAG = "InfilGlossary"