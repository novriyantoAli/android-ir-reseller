package id.co.insinyur.irreseller.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.insinyur.irreseller.db.entity.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDAO {

    @Query("SELECT * FROM `transaction` ORDER BY uid ASC LIMIT 1")
    fun getTransaction(): Transaction?

    @Query("SELECT * FROM `transaction` ORDER BY uid ASC LIMIT 1 ")
    fun getLiveTransaction(): Flow<Transaction?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("DELETE FROM `transaction`")
    suspend fun deleteAll()
}