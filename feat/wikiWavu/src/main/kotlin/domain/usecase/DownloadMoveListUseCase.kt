package domain.usecase

import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import data.remote.WavuWikiDataSource
import domain.model.Move

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(charName: String): Result<Map<String, Move>, DataError.Remote> {
        return source.fetchMoveList(charName)
            .map { dto -> dto.cargoQuery.map { it.title } }
            .map { moves ->
                val movesById = moves
                    .map { move ->
                        move.copy(
                            notes = move.notes
                                ?.cleanHtml()
                                ?.replace("\n\n", "\n")
                        )
                    }
                    .associateBy { it.id }

                movesById
                    .mapValues { (_, move) ->
                        move.copy(
                            input = move.id.substringAfter("-"),
                            level = formCompleteDataFromParent(move, movesById) { it.level },
                            damage = formCompleteDataFromParent(move, movesById) { it.damage },
                            startup = getRootStartup(move, movesById),
                        )
                    }
                    .mapKeys { (id, _) ->
                        id.substringAfter("-")
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

    /**
     * Kazuya's 112 is actually:
     *
     *  - input: ,2
     *  - damage: ,6
     *  - parent: Kazuya-1,1,
     *
     *  So we have to traverse through parents to form the complete string
     */
    private fun formCompleteDataFromParent(
        move: Move,
        movesById: Map<String, Move>,
        fieldSelector: (Move) -> String?,
    ): String? {
        var current: Move? = move
        val reverseLevel = mutableListOf<String>()

        while (current != null) {
            fieldSelector(current)?.let { reverseLevel.add(it) }
            current = current.parent?.let { parent -> movesById[parent] }
        }

        return reverseLevel
            .reversed()
            .joinToString("")
            .takeIf { it.isNotEmpty() }
    }

    /**
     * Similar to the issue above, just with startup
     */
    private fun getRootStartup(move: Move, movesById: Map<String, Move>): String? {
        var current: Move? = move
        var root: Move = move

        // Traverse up to find the topmost parent
        while (current != null) {
            root = current
            current = current.parent?.let { movesById[it] }
        }

        return root.startup
    }
}