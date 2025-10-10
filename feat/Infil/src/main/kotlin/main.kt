import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

suspend fun main() = coroutineScope {
    initKoin()
    Napier.base(DebugAntilog())

    val infilGlossary = getKoin().get<InfilGlossary>()

    launch {
        infilGlossary.subscribe()
            .collect {
                println(it)
            }
    }

    infilGlossary.fetchGlossary()

    infilGlossary.search("Block damage").also { items ->
        println(items.map { it.term })
    }


    Unit
}