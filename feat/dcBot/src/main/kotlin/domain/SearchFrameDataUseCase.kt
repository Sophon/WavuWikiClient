package domain

import WavuWikiClient
import com.example.core.domain.Result
import com.example.core.util.dropFirstAndJoin
import domain.model.Move

internal class SearchFrameDataUseCase(
    private val wavuWikiClient: WavuWikiClient,
) {
    suspend fun startWiki() {
        wavuWikiClient.fetchCompleteMoveList()
    }

    fun search(query: String): Result<Move, BotError> {
        if (isValidQuery(query).not()) {
            return Result.Error(BotError.INVALID_QUERY)
        }

        val charName = query.substringBefore(' ').replaceFirstChar { it.uppercase() }
        val move = query.dropFirstAndJoin(' ')
        return when (val result = wavuWikiClient.frameDataFor(charName = charName, move = move)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(
                when (result.error) {
                    WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                    WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                }
            )
        }
    }

    private fun isValidQuery(query: String): Boolean {
        return query.split(" ").size >= 2
    }
}