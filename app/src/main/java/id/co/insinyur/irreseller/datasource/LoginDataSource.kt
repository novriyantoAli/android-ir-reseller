package id.co.insinyur.irreseller.datasource

import id.co.insinyur.irreseller.api.LoginService
import id.co.insinyur.irreseller.model.LoginRequest

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val service: LoginService) {

    suspend fun postLogin(url: String, loginRequest: LoginRequest) = service.postLogin(url, loginRequest).await()

    suspend fun getBalance(url: String, bearer: String) = service.getBalance(url, bearer).await()

//    suspend fun login(request: LoginRequest): Result<JWT> {
//        val service = retrofit.create(LoginService::class.java)
//        return getResponse( request = { service.postLogin(request) },  defaultErrorMessage = "Error fetching Movie Description")
//    }

//    private suspend fun <T> getResponse(request: suspend () -> Response<T>, defaultErrorMessage: String): Result<T> {
//        return try {
//            Log.e("LOGIN_DATA_SOURCE","I'm working in thread ${Thread.currentThread().name}")
//            val result = request.invoke()
//            if (result.isSuccessful) {
//                return Result.success(result.body())
//            } else {
////                val errorResponse = ErrorUtils.parseError(result, retrofit)
//                Result.error( defaultErrorMessage, error = error(""))
//            }
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            Result.error("Unknown Error", null)
//        }
//    }
    fun logout() {
        // TODO: revoke authentication
    }
}