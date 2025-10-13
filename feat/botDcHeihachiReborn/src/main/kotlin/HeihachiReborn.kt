import com.example.core.domain.Result
import com.example.core.util.isAtLeast
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import domain.BotError
import domain.Command
import domain.GlossaryItem
import domain.model.Move
import domain.usecase.SearchFrameDataUseCase
import domain.usecase.SearchGlossaryUseCase
import domain.usecase.StartGlossaryUseCase
import domain.usecase.StartWikiUseCase
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
//            launch { startWikiUseCase.invoke() }
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
        val returnMessage = when (command.uppercase()) {
            Command.GL.name -> {
                val result = searchGlossaryUseCase.search(query)
                when (result) {
                    is Result.Success -> message.channel.createEmbed(embedBuilder.glossaryEmbed(result.data))
                    else -> {}
                }

                handleGlossaryResult(searchGlossaryUseCase.search(query))
            }
            else -> {
                val result = searchFrameDataUseCase.invoke(query)
                when (result) {
                    is Result.Success -> message.channel.createEmbed(embedBuilder.moveEmbed(result.data))
                    else -> {}
                }

                handleFrameDataResult(result)
            }
        }

//        message.channel.createMessage(returnMessage)
    }

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

    private fun handleFrameDataResult(result: Result<Move, BotError>): String {
        return when (result) {
            is Result.Success -> result.data.toString()
            is Result.Error -> result.error.toString()
        }
    }
}

private const val TAG = "HeihachiRebornBot"