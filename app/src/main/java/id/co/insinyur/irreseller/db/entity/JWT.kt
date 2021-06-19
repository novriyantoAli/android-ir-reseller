package id.co.insinyur.irreseller.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jwt")
class JWT(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: Int = 1,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "level")
    val level: String,

    @ColumnInfo(name = "token")
    val token: String,

    @ColumnInfo(name = "exp")
    val exp: Int
)

//"username": "rhein",
//"id": 1,
//"level": "admin",
//"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InJoZWluIiwiaWQiOjEsImxldmVsIjoiYWRtaW4iLCJ0b2tlbiI6bnVsbCwiZXhwIjoxNjE5NzgwMjQxfQ.EoHlCfXbcGZQJTsgMRKvOIn3XrSFvNqw5-L_ZgaXMyc",
//"exp": 1619780241