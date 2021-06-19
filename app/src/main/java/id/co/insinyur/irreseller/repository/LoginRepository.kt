package id.co.insinyur.irreseller.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.datasource.LoginDataSource
import id.co.insinyur.irreseller.db.AppDatabase
import id.co.insinyur.irreseller.model.Balance
import id.co.insinyur.irreseller.model.JWT
import id.co.insinyur.irreseller.model.LoginRequest
import id.co.insinyur.irreseller.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource, private val db: AppDatabase) {


    /**
     * Returns the favorite latest news applying transformations on the flow.
     * These operations are lazy and don't trigger the flow. They just transform
     * the current value emitted by the flow at that point in time.
     */

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(jwt: JWT) {
        val j = id.co.insinyur.irreseller.db.entity.JWT(
            id = jwt.id,
            username = jwt.username,
            token = "Bearer ${jwt.token}",
            level = jwt.level,
            exp = jwt.exp
        )
        db.jwtDAO().insert(j)
    }

    fun checkLogin(): Flow<id.co.insinyur.irreseller.db.entity.JWT?> {
        return db.jwtDAO().getLiveJWT()
    }

    fun postLogin(path: String, request: LoginRequest): Flow<JWT> {
        return flow {
            val app = db.appDAO().getApp()
            when (val jwt = dataSource.postLogin(Helpers.buildURL(app.url, app.port, path), request)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    insert(jwt.body)
                    emit(jwt.body)
                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    throw Exception(jwt.body?.error)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    throw Exception(jwt.error)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    throw Exception(jwt.error)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getBalance(path: String): Flow<Balance> {
        return flow {
            val app = db.appDAO().getApp()
            val jwt = db.jwtDAO().getJWT()
            when(val balance = dataSource.getBalance(Helpers.buildURL(app.url, app.port, path), jwt.token)) {
                is NetworkResponse.Success -> {
                    emit(balance.body)
                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    throw Exception(balance.body?.error)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    throw Exception(balance.error)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    throw Exception(balance.error)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun logout() = db.jwtDAO().deleteAll()
}