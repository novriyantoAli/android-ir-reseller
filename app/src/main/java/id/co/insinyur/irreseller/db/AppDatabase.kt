package id.co.insinyur.irreseller.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.co.insinyur.irreseller.db.entity.App
import id.co.insinyur.irreseller.db.entity.JWT
import id.co.insinyur.irreseller.db.entity.Transaction

@Database(entities = [App::class, JWT::class, Transaction::class], version = 3, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appDAO(): AppDAO

    abstract fun jwtDAO(): JwtDAO

    abstract fun transactionDAO(): TransactionDAO

    companion object {

        private const val DB_NAME = "irreseller.db"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, DB_NAME
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }
}