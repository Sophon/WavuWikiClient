package usecase

import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import dataLocal.GlossaryDB
import GlossaryError
import model.GlossaryItem

class CacheGlossaryUseCase(
    private val db: GlossaryDB,
) {
    suspend fun invoke(items: List<GlossaryItem>): EmptyResult<GlossaryError> {
        items.forEach { item ->
            db.insertData(term = item.term, item = item)
            item.altTerm.forEach { alias ->
                db.insertData(term = alias, item = item)
            }
        }

        return Result.Success(Unit)
    }
}