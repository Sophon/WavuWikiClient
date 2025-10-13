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

    suspend fun search(query: String): Result<GlossaryItem, BotError> {
        val cleanQuery = query.substringAfter(' ')
        return when (val result = glossary.search(cleanQuery)) {
            is Result.Success -> {
                result.data.firstOrNull()
                    ?.let { Result.Success(it) }
                    ?: Result.Error(BotError.GLOSSARY_TERM_NOT_FOUND)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                startGlossary()
                Result.Error(BotError.EMPTY_GLOSSARY)
            }
        }
    }
}

private const val TAG = "SearchGlossaryUseCase"