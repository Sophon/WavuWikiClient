import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import domain.SearchGlossaryUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import util.removeTag
import kotlin.time.Duration.Companion.seconds

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
                searchGlossaryUseCase.search(message.content.removeTag())
                    .onSuccess { glossaryItems ->
                        message.channel.createMessage(glossaryItems.firstOrNull()?.definition ?: "not found")
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { "Error: $error" }
                    }
            }
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        kord.login {
            // we need to specify this to receive the content of messages
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}

private const val TAG = "HeihachiRebornBot"