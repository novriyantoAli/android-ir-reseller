package id.co.insinyur.irreseller.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: Int = 1,

    @ColumnInfo(name = "created_at")
    val created_at: String,

    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "id_radpackage")
    val id_radpackage: Int,

    @ColumnInfo(name = "id_reseller")
    val id_reseller: Int,

    @ColumnInfo(name = "information")
    val information: String?,

    @ColumnInfo(name = "voucher")
    val voucher: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "transaction_code")
    val transaction_code: String,

    @ColumnInfo(name = "value")
    val value: Int
)