import data.InfilGlossaryDataSource
import data.InfilGlossaryDataSourceImpl
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
    )
}

val infilModule = module {
    singleOf(::InfilGlossaryDataSourceImpl).bind<InfilGlossaryDataSource>()
}