package id.co.insinyur.irreseller.api

import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.model.Balance
import id.co.insinyur.irreseller.model.ErrorResponse
import id.co.insinyur.irreseller.model.JWT
import id.co.insinyur.irreseller.model.LoginRequest
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface LoginService {

    @POST
    fun postLogin(@Url url:String, @Body body: LoginRequest): Deferred<NetworkResponse<JWT, ErrorResponse>>

    @GET
    fun getBalance(@Url url:String, @Header("Authorization") bearer: String): Deferred<NetworkResponse<Balance, ErrorResponse>>
}