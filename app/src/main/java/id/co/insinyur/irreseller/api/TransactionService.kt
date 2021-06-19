package id.co.insinyur.irreseller.api

import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.model.ErrorResponse
import id.co.insinyur.irreseller.model.MessageResponse
import id.co.insinyur.irreseller.model.TransactionResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface TransactionService {

    @GET
    fun fetch(
            @Url url: String,
            @Header("Authorization") bearer: String,
            @Query("q") query: String,
            @Query("id") id: Int,
            @Query("limit") perPage: Int
    ): Deferred<NetworkResponse<TransactionResponse, ErrorResponse>>

    @POST("api/reseller/change-package")
    @FormUrlEncoded
    fun changePackage(
            @Header("Authorization") bearer: String,
            @Field("voucher") voucher: String,
            @Field("profile") profile: String
    ): Deferred<MessageResponse>
}