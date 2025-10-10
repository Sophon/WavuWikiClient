import domain.DcConfig
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val apiKey = getApiKey()

    initKoin(apiKey)

    println("Heihachi Reborn API key: $apiKey")
}

private fun getApiKey(): String {
    val configFile = File("config.json")
    val dcConfig = Json.decodeFromString<DcConfig>(configFile.readText())

    return dcConfig.heihachiRebornApiKey
}