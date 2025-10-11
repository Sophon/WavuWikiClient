import com.example.core.coreModule
import domain.SearchGlossaryUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(
    apiKey: String,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        dcBotModule(apiKey),
        infilModule,
    )
}

fun dcBotModule(apiKey: String) = module {
    single { apiKey }

    singleOf(::HeihachiRebornImpl).bind<HeihachiReborn>()

    singleOf(::SearchGlossaryUseCase)
}