import com.example.core.coreModule
import data.WavuWikiDataSource
import data.WavuWikiDataSourceImpl
import domain.FetchMoveListUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        wavuModule,
    )
}

val wavuModule = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::FetchMoveListUseCase)
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
}