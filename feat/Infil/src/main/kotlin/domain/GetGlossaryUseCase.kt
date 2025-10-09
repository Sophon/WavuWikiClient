package domain

import com.example.core.domain.DataError
import com.example.core.domain.Result
import data.InfilGlossaryDataSource

class GetGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun execute(): Result<List<Item>, DataError.Remote> {
        return dataSource.getGlossary()
    }
}