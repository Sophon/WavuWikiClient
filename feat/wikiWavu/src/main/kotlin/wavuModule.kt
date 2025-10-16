import com.example.core.coreModule
import data.local.InMemoryMoveListDB
import data.local.MoveListDB
import data.remote.WavuWikiDataSource
import data.remote.WavuWikiDataSourceImpl
import domain.usecase.CacheMoveListUseCase
import domain.usecase.DownloadMoveListUseCase
import domain.usecase.FetchCharacterListUseCase
import domain.usecase.FetchMoveDataUseCase
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
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()

    singleOf(::FetchCharacterListUseCase)
    singleOf(::DownloadMoveListUseCase)
    singleOf(::CacheMoveListUseCase)
    singleOf(::FetchMoveDataUseCase)
}