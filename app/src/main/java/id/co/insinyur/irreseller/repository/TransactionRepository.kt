package id.co.insinyur.irreseller.repository

import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.datasource.TransactionDataSource
import id.co.insinyur.irreseller.db.AppDatabase
import id.co.insinyur.irreseller.model.MessageResponse
import id.co.insinyur.irreseller.model.TransactionResponse
import id.co.insinyur.irreseller.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TransactionRepository(private val dataSource: TransactionDataSource, private val db: AppDatabase) {

    fun getTransaction(path: String, query: String, id: Int, perPage: Int): Flow<TransactionResponse> {
        return flow {
            val app = db.appDAO().getApp()
            val jwt = db.jwtDAO().getJWT()
            when(val data = dataSource.search(Helpers.buildURL(app.url, app.port, path), jwt.token, query, id, perPage)) {
                is NetworkResponse.Success -> { emit(data.body) }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    throw Exception(data.body?.error)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    throw Exception(data.error)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    throw Exception(data.error)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun postChangeProfile(voucher: String, profile: String): Flow<MessageResponse> {
        return flow {
            val jwt = db.jwtDAO().getJWT()
            val data = dataSource.changePackage(jwt.token, voucher, profile)
            emit(data)
        }.flowOn(Dispatchers.IO)
    }
}