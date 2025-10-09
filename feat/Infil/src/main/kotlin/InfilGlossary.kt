import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InfilGlossary{
    private val outputStream = MutableStateFlow("")

    suspend fun start(): Flow<String> {
        val string = "Starting Infil Glossary"
        Napier.d(tag = TAG, message = string)

        return outputStream.apply {
            emit(string)
        }
    }
}


private const val TAG = "InfilGlossary"