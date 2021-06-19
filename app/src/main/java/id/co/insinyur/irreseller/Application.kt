package id.co.insinyur.irreseller

import android.app.Application
import id.co.insinyur.irreseller.di.appComponent
import org.koin.android.ext.android.startKoin

open class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        configureDi()
    }

    // CONFIGURATION ---
    open fun configureDi() = startKoin(this, provideComponent())

    // PUBLIC API ---
    open fun provideComponent() = appComponent
}