import domain.DcConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File

suspend fun main() = coroutineScope {
    val apiKey = getApiKey()
    Napier.base(DebugAntilog())
    initKoin(apiKey)

    val heihachi = getKoin().get<HeihachiReborn>()

    launch {
        heihachi.startSession()
    }.join()
}

//TODO: extract config to constants
private fun getApiKey(): String {
    val configFile = File("config.json")
    val dcConfig = Json.decodeFromString<DcConfig>(configFile.readText())

    return dcConfig.heihachiRebornApiKey
}