import com.example.core.coreModule
import data.local.GlossaryDB
import data.local.InMemoryGlossaryDB
import data.remote.InfilGlossaryDataSource
import data.remote.InfilGlossaryDataSourceImpl
import domain.usecase.CacheGlossaryUseCase
import domain.usecase.DownloadGlossaryUseCase
import domain.usecase.FetchDataForTermUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    //add Koin modules here
    modules(
        infilModule,
        coreModule,
    )
}

val infilModule = module {
    singleOf(::InfilGlossaryDataSourceImpl).bind<InfilGlossaryDataSource>()
    singleOf(::InfilGlossaryImpl).bind<InfilGlossary>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::DownloadGlossaryUseCase)
    singleOf(::CacheGlossaryUseCase)
    singleOf(::FetchDataForTermUseCase)
}