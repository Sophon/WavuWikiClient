package domain

import InfilGlossary
import com.example.core.domain.DataError
import com.example.core.domain.Result

internal class SearchGlossaryUseCase(
    private val glossary: InfilGlossary,
) {
    suspend fun startGlossary() {
        glossary.fetchGlossary()
    }

    fun search(query: String): Result<List<GlossaryItem>, DataError.Local> {
        return Result.Success(glossary.search(query))
    }
}