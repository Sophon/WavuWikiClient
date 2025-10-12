package domain

import InfilGlossary
import com.example.core.domain.Result
import io.github.aakira.napier.Napier

internal class SearchGlossaryUseCase(
    private val glossary: InfilGlossary,
) {
    suspend fun startGlossary() {
        glossary.fetchGlossary()
    }

    suspend fun search(query: String): Result<List<GlossaryItem>, BotError> {
        return when (val result = glossary.search(query)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                startGlossary()
                Result.Error(BotError.EMPTY_GLOSSARY)
            }
        }
    }
}

private const val TAG = "SearchGlossaryUseCase"