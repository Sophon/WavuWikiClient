
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import org.koin.java.KoinJavaComponent.getKoin

suspend fun main() = coroutineScope {
    initKoin()
    Napier.base(DebugAntilog())
    val client = getKoin().get<WavuWikiClient>()

    client.frameData(TEST_CHAR)
}


private const val TEST_CHAR = "Kaz"