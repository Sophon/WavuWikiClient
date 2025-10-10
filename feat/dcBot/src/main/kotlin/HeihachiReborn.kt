import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import util.removeTag

interface HeihachiReborn {
    suspend fun subscribeToEvents(): Flow<String>
    suspend fun startKord()
    suspend fun startSession()
}

class HeihachiRebornImpl(
    private val apiKey: String,
): HeihachiReborn {
    private val events = MutableStateFlow("") //TODO: flow of events instead of string
    private lateinit var kord: Kord


    override suspend fun startSession() {
        //kord stuff
        //glossary stuff
    }

    override suspend fun subscribeToEvents(): Flow<String> {
        return events.apply {
            emit("API key: $apiKey")
        }
    }

    override suspend fun startKord() {
        kord = Kord(token = apiKey)

        kord.on<MessageCreateEvent> {
            // ignore other bots, even ourselves. We only serve humans here!
            if (message.author?.isBot != false) return@on

            if (message.content == "!test") {
                message.channel.createMessage("Replying to test")
                events.emit("test command")
            }

            if (kord.selfId in message.mentionedUserIds) {
                message.channel.createMessage(message.content.removeTag())
            }
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        coroutineScope {
            launch {
                kord.login {
                    // we need to specify this to receive the content of messages
                    @OptIn(PrivilegedIntent::class)
                    intents += Intent.MessageContent
                }
            }
        }
    }
}

private const val TAG = "HeihachiRebornBot"