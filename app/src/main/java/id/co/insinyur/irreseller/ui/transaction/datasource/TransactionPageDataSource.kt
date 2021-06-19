package id.co.insinyur.irreseller.ui.transaction.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.repository.TransactionRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TransactionPageDataSource(
    private val repository: TransactionRepository,
    private val url: String,
    private val query: String,
    private val id: Int,
    private val perPage: Int,
    private val scope: CoroutineScope): PageKeyedDataSource<Int, Transaction>(){

    // for data
    private val supervisorJob = SupervisorJob()
    private val networkState = MutableLiveData<NetworkState>()
    private var retryQuery: (() -> Any)? = null // Keep reference of the last query(

    override fun loadInitial(p: LoadInitialParams<Int>, c: LoadInitialCallback<Int, Transaction>) {
        retryQuery = { loadInitial(p, c) }
        Log.e("LOAD INITIAL CALLED", "LOAD INITIAL MUST NO CALL IN SEARCH")
        executeQuery(this.id, this.perPage){
            val next = if (it.isEmpty()){ 0 } else { it.last().id }
            c.onResult(it, null, next)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Transaction>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Transaction>) {
        val page = params.key
        retryQuery = { loadAfter(params, callback) }
        Log.e("PAGE", "LOAD AFTER PAGE $page")
        Log.e("REUQEST SIZE", "LOAD AFTER LOAD SIZE" + params.requestedLoadSize)
        executeQuery(page, params.requestedLoadSize) {
            if (page == it.last().id){
                return@executeQuery
            }
            callback.onResult(it, it.last().id)
        }
    }

        // UTILS ---
    private fun executeQuery(id: Int, perPage: Int, callback:(List<Transaction>) -> Unit) {
        scope.launch(getJobErrorHandler() + supervisorJob) {
            repository.getTransaction(url, query, id, perPage).onStart {
                networkState.postValue(NetworkState.RUNNING)
            }.catch {
                it.printStackTrace()
                networkState.postValue(NetworkState.FAILED)
            }.collect {
                retryQuery = null
                networkState.postValue(NetworkState.SUCCESS)
                callback(it.data)
            }
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(TransactionPageDataSource::class.java.simpleName, "An error happened: $e")
        networkState.postValue(NetworkState.FAILED)
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
        // Cancel possible running job to only keep last result searched by user
    }

    // PUBLIC API ---
    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun refresh() = this.invalidate()

    fun retryFailedQuery() {
        val prevQuery = retryQuery
        retryQuery = null
        prevQuery?.invoke()
    }
}

//class UserDataSource(private val repository: UserRepository,
//                     private val query: String,
//                     private val sort: String,
//                     private val scope: CoroutineScope
//): PageKeyedDataSource<Int, User>() {
//
//    // FOR DATA ---
//    private var supervisorJob = SupervisorJob()
//    private val networkState = MutableLiveData<NetworkState>()
//    private var retryQuery: (() -> Any)? = null // Keep reference of the last query (to be able to retry it if necessary)
//
//    // OVERRIDE ---
//    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
//        retryQuery = { loadInitial(params, callback) }
//        executeQuery(1, params.requestedLoadSize) {
//            callback.onResult(it, null, 2)
//        }
//    }
//
//    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
//        val page = params.key
//        retryQuery = { loadAfter(params, callback) }
//        executeQuery(page, params.requestedLoadSize) {
//            callback.onResult(it, page + 1)
//        }
//    }
//
//    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) { }
//
//    // UTILS ---
//    private fun executeQuery(page: Int, perPage: Int, callback:(List<User>) -> Unit) {
//        networkState.postValue(NetworkState.RUNNING)
//        scope.launch(getJobErrorHandler() + supervisorJob) {
//            delay(200) // To handle user typing case
//            val users = repository.searchUsersWithPagination(query, page, perPage, sort)
//            retryQuery = null
//            networkState.postValue(NetworkState.SUCCESS)
//            callback(users)
//        }
//    }
//
//    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
//        Log.e(UserDataSource::class.java.simpleName, "An error happened: $e")
//        networkState.postValue(NetworkState.FAILED)
//    }
//
//    override fun invalidate() {
//        super.invalidate()
//        supervisorJob.cancelChildren()   // Cancel possible running job to only keep last result searched by user
//    }
//
//    // PUBLIC API ---
//
//    fun getNetworkState(): LiveData<NetworkState> =
//        networkState
//
//    fun refresh() =
//        this.invalidate()
//
//    fun retryFailedQuery() {
//        val prevQuery = retryQuery
//        retryQuery = null
//        prevQuery?.invoke()
//    }
//}