package id.co.insinyur.irreseller.di

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import id.co.insinyur.irreseller.api.LoginService
import id.co.insinyur.irreseller.api.PackageService
import id.co.insinyur.irreseller.api.TransactionService
import id.co.insinyur.irreseller.api.UserService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    factory<Interceptor> {
        HttpLoggingInterceptor { Log.e("API", it) }
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    factory { OkHttpClient.Builder()
            .addInterceptor(get())
            .callTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build() }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("http://172.0.0.2:3031/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
    }

    factory {
        get<Retrofit>().create(PackageService::class.java)
    }
    factory {
        get<Retrofit>().create(LoginService::class.java)
    }
    factory {
        get<Retrofit>().create(TransactionService::class.java)
    }
}