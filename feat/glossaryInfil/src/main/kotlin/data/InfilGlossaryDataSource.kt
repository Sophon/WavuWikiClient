package data

import BASE_URL
import io.ktor.client.HttpClient
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.network.safeCall
import domain.GlossaryItem
import io.ktor.client.request.get

internal interface InfilGlossaryDataSource {
    suspend fun getGlossary(): Result<List<GlossaryItemDto>, DataError.Remote>
}

internal class InfilGlossaryDataSourceImpl(
    private val httpClient: HttpClient
): InfilGlossaryDataSource {
    override suspend fun getGlossary(): Result<List<GlossaryItemDto>, DataError.Remote> {
        return safeCall { httpClient.get(BASE_URL) }
    }
}