import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
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
        color = Color(COLOR)

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
}

private const val COLOR = 0x00FF00