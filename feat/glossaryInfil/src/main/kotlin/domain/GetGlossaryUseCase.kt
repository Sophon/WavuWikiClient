package domain

import com.example.core.domain.DataError
import com.example.core.domain.Result
import data.InfilGlossaryDataSource

internal class GetGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun execute(): Result<List<GlossaryItem>, DataError.Remote> {
        return dataSource.getGlossary()
    }
}