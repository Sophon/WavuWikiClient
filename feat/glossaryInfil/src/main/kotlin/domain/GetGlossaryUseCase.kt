package domain

import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import data.InfilGlossaryDataSource

internal class GetGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun execute(): Result<List<GlossaryItem>, DataError.Remote> {
        return dataSource.getGlossary()
            .map { items ->
                items.map { item ->
                    GlossaryItem(
                        term = item.term,
                        definition = item.def,
                        altTerm = item.altterm,
                        videos = item.videos,
                        games = item.games,
                        jpTranslation = item.jp
                            ?.split("<br>")
                            ?: listOf()
                    )
                }
            }
    }
}