import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface HeihachiReborn {
    suspend fun subscribe(): Flow<String>
}

class HeihachiRebornImpl(
    private val apiKey: String,
): HeihachiReborn {
    private val outputStream = MutableStateFlow("")

    override suspend fun subscribe(): Flow<String> {
        return outputStream.apply {
            emit("API key: $apiKey")
        }
    }
}