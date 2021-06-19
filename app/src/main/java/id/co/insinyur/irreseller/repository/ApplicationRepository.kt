package id.co.insinyur.irreseller.repository

import id.co.insinyur.irreseller.db.AppDatabase
import id.co.insinyur.irreseller.db.entity.App
import kotlinx.coroutines.flow.Flow

class ApplicationRepository(private val db: AppDatabase) {

    suspend fun save(url: String, port: String) {
        db.appDAO().insert(App(url = url, port = port))
    }

    suspend fun load(): Flow<App?> {
        return db.appDAO().getLiveApp()
    }
}