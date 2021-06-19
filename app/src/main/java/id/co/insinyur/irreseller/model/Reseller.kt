package id.co.insinyur.irreseller.model

data class Reseller(
    val created_at: String,
    val id: Int,
    val level: String,
    val password: Any,
    val username: String
)