package usecase

import com.example.core.domain.Result
import com.example.core.domain.map
import com.example.core.util.removeWhiteSpace
import dataLocal.GlossaryDB
import GlossaryError
import model.GlossaryItem

class FetchDataForTermUseCase(
    private val db: GlossaryDB,
) {
    suspend fun invoke(query: String): Result<List<GlossaryItem>, GlossaryError> {
        return db.fetchDataFor(query)
            .map { items ->
                items
                    .distinctBy { it.term }
                    .sortedByDescending {
                        it.term.equals(query, ignoreCase = true) ||
                                it.term.equals(query.removeWhiteSpace(), ignoreCase = true)
                    }
            }
    }
}