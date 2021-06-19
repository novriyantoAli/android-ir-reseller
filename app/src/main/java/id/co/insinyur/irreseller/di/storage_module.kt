package id.co.insinyur.irreseller.di

import id.co.insinyur.irreseller.db.AppDatabase
import id.co.insinyur.irreseller.storage.SharedPrefsManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val storageModule = module {
    single {
        AppDatabase(androidContext())
    }
    single {
        SharedPrefsManager(androidContext())
    }
}