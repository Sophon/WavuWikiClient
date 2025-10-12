import com.example.core.domain.Result
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import domain.GlossaryItem
import domain.SearchGlossaryUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import util.removeTag

interface HeihachiReborn {
    suspend fun startSession()
}

internal class HeihachiRebornImpl(
    private val apiKey: String,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
): HeihachiReborn {
    private val events = MutableStateFlow("") //TODO: flow of events instead of string
    private lateinit var kord: Kord


    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        searchGlossaryUseCase.startGlossary()
        startKord()
    }

    private suspend fun startKord() {
        kord = Kord(token = apiKey)

        kord.on<MessageCreateEvent> {
            // ignore other bots, even ourselves. We only serve humans here!
            if (message.author?.isBot != false) return@on

            if (message.content == "!test") {
                message.channel.createMessage("Replying to test")
                events.emit("test command")
            }

            if (kord.selfId in message.mentionedUserIds) {
                val command: String
                val query: String
                message.content.removeTag().split(" ").also { chunks ->
                    if (isValidQuery(chunks).not()) return@on
                    command = chunks.first()
                    query = chunks.drop(1).joinToString(" ")
                }

                val returnMessage = when (command) {
                    "fd" -> {
                        "TODO"
                    }
                    "gl" -> {
                        glossarySearch(query).firstOrNull()?.definition ?: "not found"
                    }
                    else -> {
                        "unsupported"
                    }
                }

                message.channel.createMessage(returnMessage)
            }
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        kord.login {
            // we need to specify this to receive the content of messages
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    private fun isValidQuery(query: List<String>): Boolean {
        return query.size >= 2
    }

    private fun glossarySearch(query: String): List<GlossaryItem> {
        val result = searchGlossaryUseCase.search(query)

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Napier.e(tag = TAG) { "Error: ${result.error}" }
                emptyList()
            }
        }
    }
}

private const val TAG = "HeihachiRebornBot"