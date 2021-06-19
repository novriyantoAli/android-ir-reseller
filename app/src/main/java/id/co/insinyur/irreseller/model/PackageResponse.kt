package id.co.insinyur.irreseller.model

import com.google.gson.annotations.SerializedName

data class PackageResponse(
    @SerializedName("data")
    val packages: List<Package>,

    val total_page: Int
)