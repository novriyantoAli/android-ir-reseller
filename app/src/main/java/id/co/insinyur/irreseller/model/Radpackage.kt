package id.co.insinyur.irreseller.model

data class Radpackage(
    val id: Int,
    val id_package: Int,
    val `package`: Package,
    val username: String,
    val users: List<UserX>?
)