package domain.usecase

import WavuWikiClient
import com.example.core.domain.Result
import com.example.core.util.dropFirstAndJoin
import domain.BotError
import domain.WavuError
import domain.model.Move

internal class SearchFrameDataUseCase(
    private val wavuWikiClient: WavuWikiClient,
) {
    fun invoke(query: String): Result<Move, BotError> {
        val parsedQuery = parseQuery(query)
        if (parsedQuery == null) return Result.Error(BotError.INVALID_QUERY)

        return when (
            val result = wavuWikiClient.frameDataFor(charName = parsedQuery.charName, move = parsedQuery.move)
        ) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(
                when (result.error) {
                    WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                    WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                }
            )
        }
    }

    private fun parseQuery(query: String): ParsedQuery? {
        if (query.split(" ").size < 2) return null

        val charName = query.substringBefore(' ').replaceFirstChar { it.uppercase() }
        val move = query.dropFirstAndJoin(' ')

        return ParsedQuery(charName, move)
    }

    private data class ParsedQuery(
        val charName: String,
        val move: String,
    )
}