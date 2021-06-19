package id.co.insinyur.irreseller.model

data class Transaction(
    val created_at: String,
    val id: Int,
    val id_radpackage: Int,
    val id_reseller: Int,
    val information: String?,
    val radpackage: Radpackage?,
    val reseller: Reseller,
    val status: String,
    val transaction_code: String,
    val value: Int
)