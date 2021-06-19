package id.co.insinyur.irreseller.api

import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.model.ErrorResponse
import id.co.insinyur.irreseller.model.PackageResponse
import id.co.insinyur.irreseller.model.Transaction
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface PackageService {
    @GET
    fun fetch(
            @Url url: String,
            @Header("Authorization") bearer: String,
            @Query("q") query: String,
            @Query("id") id: Int,
            @Query("limit") limit: Int
    ): Deferred<NetworkResponse<PackageResponse, ErrorResponse>>

    @POST
    @FormUrlEncoded
    fun buy(
            @Url url: String,
            @Header("Authorization") auth: String,
            @Field("idPackage") idPackage: Int
    ): Deferred<NetworkResponse<Transaction, ErrorResponse>>
}