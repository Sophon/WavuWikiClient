import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import domain.GetGlossaryUseCase
import domain.Item
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InfilGlossary(
    private val getGlossaryUseCase: GetGlossaryUseCase,
) {
    private val outputStream = MutableStateFlow("")
    private val glossary = mutableListOf<Item>()

    suspend fun subscribe(): Flow<String> {
        val string = "Starting Infil Glossary"
        Napier.d(tag = TAG, message = string)

        return outputStream.apply {
            emit(string)
        }
    }

    suspend fun fetchGlossary() {
        getGlossaryUseCase.execute()
            .onSuccess { items ->
                glossary.addAll(items)
                outputStream.emit("Successfully retrieved: ${glossary.size}")
            }
            .onError { errorType ->
                Napier.e(tag = TAG) { "Error: $errorType" }
                outputStream.emit("Error: $errorType")
            }
    }
}


private const val TAG = "InfilGlossary"