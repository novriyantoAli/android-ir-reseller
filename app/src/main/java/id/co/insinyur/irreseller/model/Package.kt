package id.co.insinyur.irreseller.model

data class Package(
    val created_at: String,
    val id: Int,
    val margin: Int,
    val name: String,
    val price: Int,
    val profile: String,
    val validity_unit: String,
    val validity_value: Int
)