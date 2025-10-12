import com.example.core.domain.Result
import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import domain.BotError
import domain.SearchFrameDataUseCase
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
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
): HeihachiReborn {
    private val events = MutableStateFlow("") //TODO: flow of events instead of string
    private lateinit var kord: Kord


    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        //TODO: do the two starts concurrently
        searchGlossaryUseCase.startGlossary()
        searchFrameDataUseCase.startWiki()
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
                val pureMessage = message.content.removeTag()
                message.content.removeTag().split(" ").also { chunks ->
                    if (chunks.isValid().not()) return@on
                    command = chunks.first()
                    query = chunks.drop(1).joinToString(" ")
                }

                val returnMessage = when (command) {
                    "gl" -> {
                        when (val result = searchGlossaryUseCase.search(query)) {
                            is Result.Success -> {
                                result.data.firstOrNull()?.definition
                                    ?: "not found"
                            }
                            is Result.Error -> {
                                if (result.error == BotError.EMPTY_GLOSSARY) {
                                    "try again later"
                                } else {
                                    result.error.toString()
                                }
                            }
                        }
                    }
                    else -> {
                        searchFrameDataUseCase.search(pureMessage)
                            .onSuccess { it.toString() }
                            .onError { it.toString() }

                        when (val result = searchFrameDataUseCase.search(query)) {
                            is Result.Success -> result.data.toString()
                            is Result.Error -> result.error.toString()
                        }
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

    private fun List<String>.isValid(): Boolean = this.size >= 2
}

private const val TAG = "HeihachiRebornBot"