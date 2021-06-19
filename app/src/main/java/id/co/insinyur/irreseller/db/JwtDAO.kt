package id.co.insinyur.irreseller.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.insinyur.irreseller.db.entity.JWT
import kotlinx.coroutines.flow.Flow

@Dao
interface JwtDAO {

    @Query("SELECT * FROM jwt ORDER BY uid ASC LIMIT 1")
    fun getJWT(): JWT

    @Query("SELECT * FROM jwt ORDER BY uid ASC LIMIT 1 ")
    fun getLiveJWT(): Flow<JWT?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(JWT: JWT)

    @Query("DELETE FROM jwt")
    suspend fun deleteAll()
}