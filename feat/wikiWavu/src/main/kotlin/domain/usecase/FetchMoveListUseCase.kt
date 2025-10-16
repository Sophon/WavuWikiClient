package domain.usecase

import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import data.WavuWikiDataSource
import domain.model.Move

internal class FetchMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun execute(char: String): Result<Map<String, Move>, DataError.Remote> {
        return source.fetchMoveList(char)
            .map { dto -> dto.cargoQuery.map { it.title } }
            .map { moves ->
                moves
                    .map { move ->
                        move.copy(
                            notes = move.notes?.cleanHtml()
                        )
                    }
                    .associateBy { move ->
                        move.id.substringAfter("-")
                    }
            }
    }

    private fun String.cleanHtml(): String {
        return this
            .decodeHtmlEntities()
            .removeHtmlTags()
            .replace(Regex("\\*\\s*\\n"), "* ")
            .trim()
    }

    private fun String.decodeHtmlEntities(): String {
        return this
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&nbsp;", " ")
    }

    private fun String.removeHtmlTags(): String {
        return this.replace(Regex("<[^>]*>"), "")
    }
}