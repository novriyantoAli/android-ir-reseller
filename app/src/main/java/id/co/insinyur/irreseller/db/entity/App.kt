package id.co.insinyur.irreseller.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app")
class App(
        @PrimaryKey
        @ColumnInfo(name = "uid")
        val uid: Int = 1,

        @ColumnInfo(name = "hostname")
        val url: String,

        @ColumnInfo(name = "port")
        val port: String
)