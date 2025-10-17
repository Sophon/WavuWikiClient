package domain.usecase

import com.example.core.domain.Result
import data.remote.InfilGlossaryDataSource
import domain.GlossaryError
import domain.GlossaryItem

internal class DownloadGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun invoke(): Result<List<GlossaryItem>, GlossaryError> {
        return when (val result = dataSource.getGlossary()) {
            is Result.Success -> {
                val data = result.data.map { item ->
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

                Result.Success(data)
            }
            is Result.Error -> {
                Result.Error(GlossaryError.ERROR_DOWNLOADING_DATA)
            }
        }
    }
}