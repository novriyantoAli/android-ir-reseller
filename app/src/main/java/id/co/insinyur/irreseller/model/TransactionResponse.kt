package id.co.insinyur.irreseller.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("data")
    val `data`: List<Transaction>,
    val total_page: Int
)