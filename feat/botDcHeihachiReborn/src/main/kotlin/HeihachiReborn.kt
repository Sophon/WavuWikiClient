import com.example.core.domain.Result
import com.example.core.util.isAtLeast
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import BotError
import model.Command
import usecase.SearchFrameDataUseCase
import usecase.SearchGlossaryUseCase
import usecase.StartGlossaryUseCase
import usecase.StartWikiUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import util.removeTag
import kotlin.time.ExperimentalTime

interface HeihachiReborn {
    suspend fun startSession()
}

internal class HeihachiRebornImpl(
    private val apiKey: String,
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val startWikiUseCase: StartWikiUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val embedBuilder: EmbedBuilder,
): HeihachiReborn {
    private lateinit var kord: Kord


    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        coroutineScope {
            launch { startGlossaryUseCase.invoke() }
            launch { startWikiUseCase.invoke() }
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

    @OptIn(ExperimentalTime::class)
    private suspend fun MessageCreateEvent.handleMessage() {
        if (message.content == "!test") {
            message.channel.createMessage("Replying to test")
            message.channel.createEmbed(embedBuilder.testEmbed())
            return
        }

        if (kord.selfId !in message.mentionedUserIds) return

        val query = message.content.removeTag().takeIf { it.isAtLeast(2) } ?: return
        val command = query.substringBefore(' ')

        //either a command or frame-data query
        when (command.uppercase()) {
            Command.GL.name -> {
                handleResult(searchGlossaryUseCase.search(query)) { glossaryItem ->
                    embedBuilder.glossaryEmbed(glossaryItem)
                }
            }
            else -> {
                handleResult(searchFrameDataUseCase.invoke(query)) { move ->
                    embedBuilder.moveEmbed(move)
                }
            }
        }
    }

    private suspend fun <T, E: BotError> MessageCreateEvent.handleResult(
        result: Result<T, E>,
        createEmbed: (T) -> dev.kord.rest.builder.message.EmbedBuilder.() -> Unit,
    ) {
        when (result) {
            is Result.Success -> message.channel.createEmbed(createEmbed(result.data))
            is Result.Error -> message.channel.createEmbed(embedBuilder.errorEmbed(result.error))
        }
    }
}

private const val TAG = "HeihachiRebornBot"