package data

import BASE_URL
import io.ktor.client.HttpClient
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.network.safeCall
import io.ktor.client.request.get

interface InfilGlossaryDataSource {
    suspend fun getGlossary(): Result<GlossaryResponseDto, DataError.Remote>
}

class InfilGlossaryDataSourceImpl(
    private val httpClient: HttpClient
): InfilGlossaryDataSource {
    override suspend fun getGlossary(): Result<GlossaryResponseDto, DataError.Remote> {
        return safeCall { httpClient.get(BASE_URL) }
    }
}