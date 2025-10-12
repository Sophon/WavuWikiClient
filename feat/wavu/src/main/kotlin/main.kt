
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import org.koin.java.KoinJavaComponent.getKoin

suspend fun main() = coroutineScope {
    initKoin()
    Napier.base(DebugAntilog())
    val client = getKoin().get<WavuWikiClient>()

    client.fetchCompleteMoveList()

    println("========")

    val move = client.frameDataFor(
        charName = "Kazuya",
        move = "1,1,2"
    )
    println("\n\n\ndata: $move")
}
