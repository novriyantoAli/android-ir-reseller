package id.co.insinyur.irreseller.model

import com.google.gson.annotations.SerializedName

data class JWT(
        @SerializedName("id") val id: Int,
        @SerializedName("username") val username: String,
        @SerializedName("level") val level: String,
        @SerializedName("token") val token: String,
        @SerializedName("exp") val exp: Int,
)