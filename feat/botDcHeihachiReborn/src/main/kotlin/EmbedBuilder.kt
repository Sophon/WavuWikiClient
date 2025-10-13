import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import domain.BotError
import domain.GlossaryItem
import domain.model.Move

class EmbedBuilder {
    fun testEmbed(): EmbedBuilder.() -> Unit = {
        title = "Test Embed"
        description = "This is a test embed message"
        color = Color(0x00FF00) // Green color

        field {
            name = "Field 1"
            value = "This is a field value"
            inline = false
        }

        field {
            name = "Field 2"
            value = "Inline field"
            inline = true
        }

        footer {
            text = "Test Footer"
        }
    }

    fun moveEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = move.characterName //TODO: clickable
        description = move.input //TODO: clickable
        color = Color(GREEN)

        field(name = "Level", value = move.level,)
        field(name = "Damage", value = move.damage.orEmpty(),)
        field(name = "Startup", value = move.startup,)
        field(name = "OB", value = move.block,)
        field(name = "OH", value = move.hit,)
        field(name = "CH", value = move.ch ?: move.hit,)

        field(
            name = "Notes",
            value = move.notes,
            inline = false,
        )

        //TODO: clickable
        //TODO: image
        //TODO: feedback command
        footer {
            text = "Wavu Wiki"
        }
    }

    fun glossaryEmbed(item: GlossaryItem): EmbedBuilder.() -> Unit = {
        val formattedItem = item.format()
        title = formattedItem.term
        color = Color(ORANGE)

        field(name = "", value = formattedItem.definition.replaceUnderline(), inline = false)

        val japaneseValueString = formattedItem.jpTranslation
            .joinToString(separator = "") { "* $it\n" }
        field(name = "🇯🇵", value = japaneseValueString, inline = false)

        footer {
            text = "Infil Glossary"
        }
    }

    fun errorEmbed(error: BotError): EmbedBuilder.() -> Unit = {
        title = "Error"
        description = error.toString()
    }


    private fun EmbedBuilder.field(
        name: String,
        value: String?,
        inline: Boolean = true,
    ) {
        field {
            this.name = name
            this.value = value.orEmpty()
            this.inline = inline
        }
    }

    private fun GlossaryItem.format(): GlossaryItem {
        return this.copy(
            definition = this.definition.replaceUnderline(),
            jpTranslation = this.jpTranslation.map { it.replaceItalic() }
        )
    }

    private fun String.replaceItalic(): String {
        return this
            .replace("<em>", "*")
            .replace("</em>", "*")
    }

    /**
     * Replaces HTML tags with Markdown.
     *
     * Examples:
     * - `!<'block'>` → `__block__`
     * - `!<'whiff punish','whiff'>` → `__whiff__`
     *
     * Takes the last comma-separated value if multiple are present.
     */
    private fun String.replaceUnderline(): String {
        return this.replace(Regex("!<'([^']+)'(?:,'[^']*')*>")) { matchResult ->
            val content = matchResult.groupValues[1]
            val words = content.split("','")
            val lastWord = words.last()
            "**__${lastWord}__**"
        }
    }
}

private const val GREEN = 0x00FF00
private const val ORANGE = 0x00FF6400