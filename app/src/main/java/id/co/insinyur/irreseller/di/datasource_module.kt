package id.co.insinyur.irreseller.di

import id.co.insinyur.irreseller.datasource.LoginDataSource
import id.co.insinyur.irreseller.datasource.PackageDataSource
import id.co.insinyur.irreseller.datasource.TransactionDataSource
import org.koin.dsl.module.module

val datasourceModule = module {
    single { LoginDataSource(get()) }
    single { TransactionDataSource(get()) }
    single { PackageDataSource(get())}
}