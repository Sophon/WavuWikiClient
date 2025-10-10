import domain.DcConfig
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File

suspend fun main() = coroutineScope {
    val apiKey = getApiKey()

    initKoin(apiKey)

    val heihachi = getKoin().get<HeihachiReborn>()

    launch {
        heihachi.subscribe()
            .collect {
                println(it)
            }
    }


    Unit
}

private fun getApiKey(): String {
    val configFile = File("config.json")
    val dcConfig = Json.decodeFromString<DcConfig>(configFile.readText())

    return dcConfig.heihachiRebornApiKey
}