import com.example.core.domain.Result
import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import domain.BotError
import domain.GlossaryItem
import domain.SearchFrameDataUseCase
import domain.SearchGlossaryUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import util.removeTag

interface HeihachiReborn {
    suspend fun startSession()
}

internal class HeihachiRebornImpl(
    private val apiKey: String,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
): HeihachiReborn {
    private lateinit var kord: Kord


    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        coroutineScope {
            launch { searchGlossaryUseCase.startGlossary() }
            launch { searchFrameDataUseCase.startWiki() }
        }
        startKord()
    }


    private suspend fun startKord() {
        kord = Kord(token = apiKey)

        kord.on<MessageCreateEvent> {
            // ignoring other bots, even ourselves. We only serve humans here!
            if (message.author?.isBot != false) return@on

            handleMessage()
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        kord.login {
            // we need to specify this to receive the content of messages
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    private suspend fun MessageCreateEvent.handleMessage() {
        if (message.content == "!test") {
            message.channel.createMessage("Replying to test")
            return
        }

        if (kord.selfId !in message.mentionedUserIds) return

        val command: String
        val query: String
        val pureMessage = message.content.removeTag()
        message.content.removeTag().split(" ").also { chunks ->
            if (chunks.isValid().not()) return
            command = chunks.first()
            query = chunks.drop(1).joinToString(" ")
        }

        val returnMessage = when (command) {
            "gl" -> handleGlossaryResult(searchGlossaryUseCase.search(query))
            else -> searchFrameData(pureMessage)
        }

        message.channel.createMessage(returnMessage)
    }

    private fun List<String>.isValid(): Boolean = this.size >= 2

    private fun handleGlossaryResult(result: Result<GlossaryItem, BotError>): String {
        return when (result) {
            is Result.Success -> result.data.definition
            is Result.Error -> {
                when (result.error) {
                    BotError.EMPTY_GLOSSARY -> "Try again later."
                    BotError.GLOSSARY_TERM_NOT_FOUND -> "Term not found."
                    else -> ""
                }
            }
        }
    }

    private fun searchFrameData(query: String): String {
        searchFrameDataUseCase.search(query)
            .onSuccess { it.toString() }
            .onError { it.toString() }

        return when (val result = searchFrameDataUseCase.search(query)) {
            is Result.Success -> result.data.toString()
            is Result.Error -> result.error.toString()
        }
    }
}

private const val TAG = "HeihachiRebornBot"