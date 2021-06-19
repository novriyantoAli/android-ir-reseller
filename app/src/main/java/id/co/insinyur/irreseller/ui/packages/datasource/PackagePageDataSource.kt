package id.co.insinyur.irreseller.ui.packages.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.model.Package
import id.co.insinyur.irreseller.repository.PackageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart

class PackagePageDataSource(
    private val repository: PackageRepository,
    private val url: String,
    private val query: String,
    private val id: Int,
    private val perPage: Int,
    private val scope: CoroutineScope
): PageKeyedDataSource<Int, Package>(){

    // for data
    private val supervisorJob = SupervisorJob()
    private val networkState = MutableLiveData<NetworkState>()
    private var retryQuery: (() -> Any)? = null // Keep reference of the last query(

    override fun loadInitial(p: LoadInitialParams<Int>, c: LoadInitialCallback<Int, Package>) {
        retryQuery = { loadInitial(p, c) }
        executeQuery(url, this.id, this.perPage){
            val next = if (it.isEmpty()){ 0 } else { it.last().id }
            c.onResult(it, null, next)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Package>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Package>) {
        val page = params.key
        retryQuery = { loadAfter(params, callback) }
        executeQuery(url, page, params.requestedLoadSize) {
            if (page == it.last().id){
                return@executeQuery
            }
            callback.onResult(it, it.last().id)
        }
    }

    // UTILS ---
    private fun executeQuery(url: String, id: Int, perPage: Int, callback:(List<Package>) -> Unit) {
        scope.launch(getJobErrorHandler() + supervisorJob) {
            repository.getPackage(url, query, id, perPage).onStart {
                networkState.postValue(NetworkState.RUNNING)
            }.catch {
                it.printStackTrace()
                networkState.postValue(NetworkState.FAILED)
            }.collect {
                retryQuery = null
                networkState.postValue(NetworkState.SUCCESS)
                callback(it.packages)
            }
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(PackagePageDataSource::class.java.simpleName, "An error happened: $e")
        e.printStackTrace()
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