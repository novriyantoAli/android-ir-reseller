package id.co.insinyur.irreseller.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.insinyur.irreseller.db.entity.App
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {

    @Query("SELECT * FROM app ORDER BY uid ASC LIMIT 1")
    fun getApp(): App

    @Query("SELECT * FROM app ORDER BY uid ASC LIMIT 1 ")
    fun getLiveApp(): Flow<App?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: App)

    @Query("DELETE FROM app")
    suspend fun deleteAll()
}