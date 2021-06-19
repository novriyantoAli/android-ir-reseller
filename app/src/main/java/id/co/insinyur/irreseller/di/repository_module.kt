package id.co.insinyur.irreseller.di

import id.co.insinyur.irreseller.repository.*
import org.koin.dsl.module.module

val repositoryModule = module {
    factory { ApplicationRepository(get()) }
    factory { LoginRepository(get(), get()) }
    factory { TransactionRepository(get(), get()) }
    factory { PackageRepository(get(), get()) }
//    factory { UserRepository(get()) }
}