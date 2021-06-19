package id.co.insinyur.irreseller.repository

import com.haroldadmin.cnradapter.NetworkResponse
import id.co.insinyur.irreseller.datasource.PackageDataSource
import id.co.insinyur.irreseller.db.AppDatabase
import id.co.insinyur.irreseller.model.PackageResponse
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PackageRepository(private val dataSource: PackageDataSource, private val db: AppDatabase) {

    fun getPackage(path: String, query: String, id: Int, perPage: Int): Flow<PackageResponse> {
        return flow {
            val app = db.appDAO().getApp()
            val jwt = db.jwtDAO().getJWT()
            when(val data = dataSource.search(Helpers.buildURL(app.url, app.port, path), jwt.token, query, id, perPage)) {
                is NetworkResponse.Success -> {
                    emit(data.body)
                }
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

    fun postBuy(path: String, idPackage: Int): Flow<Transaction> {
        return flow {
            val app = db.appDAO().getApp()
            val jwt = db.jwtDAO().getJWT()
            val data = dataSource.buy(Helpers.buildURL(app.url, app.port, path), jwt.token, idPackage)
            // save to local before send it to vieModel
            when (data) {
                is NetworkResponse.Success -> {
                    val transaction = id.co.insinyur.irreseller.db.entity.Transaction(
                        created_at = data.body.created_at,
                        id = data.body.id,
                        id_radpackage = data.body.id_radpackage,
                        id_reseller = data.body.id_reseller,
                        information = data.body.information,
                        voucher = data.body.radpackage?.username.toString(),
                        status = data.body.status,
                        transaction_code = data.body.transaction_code,
                        value = data.body.value
                    )
                    db.transactionDAO().insert(transaction)
                    emit(data.body)
                }
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
}