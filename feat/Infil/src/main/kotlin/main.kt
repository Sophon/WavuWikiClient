import kotlinx.coroutines.coroutineScope

suspend fun main() = coroutineScope {
    val glossary = InfilGlossary()

    glossary.start()
        .collect {
            println(it)
        }
}