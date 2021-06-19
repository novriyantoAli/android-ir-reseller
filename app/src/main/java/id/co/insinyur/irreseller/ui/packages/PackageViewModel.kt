package id.co.insinyur.irreseller.ui.packages

import android.app.Application
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.base.BaseViewModel
import id.co.insinyur.irreseller.model.Package
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.repository.PackageRepository
import id.co.insinyur.irreseller.ui.packages.datasource.PackagePageDataSourceFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class PackageViewModel(private val repository: PackageRepository, app: Application): BaseViewModel() {
    // FOR DATA ---
    private val pageDataSource = PackagePageDataSourceFactory(
        repository = repository,
        scope = ioScope,
        path = app.resources.getStringArray(R.array.urls)[2]
    )

    // OBSERVABLES ---
    val packages = LivePagedListBuilder(pageDataSource, pagedListConfig()).build()
    val networkState: LiveData<NetworkState>? = Transformations.switchMap(pageDataSource.source) { it.getNetworkState() }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _buy = MutableLiveData<Package>()
    val buy: LiveData<Transaction> = _buy.switchMap { pac ->
        liveData {
            repository.postBuy(
                    path = app.resources.getStringArray(R.array.urls)[3], idPackage = pac.id
            ).onStart {  }
                .catch { _message.value = it.localizedMessage }
                .onCompletion {  }
                .collect { emit(it) }
        }
    }
    // PUBLIC API ---

    /**
     * Fetch a list of [User] by name
     * Called each time an user hits a key through [SearchView].
     */
    fun fetch(query: String) {
        val search = query.trim()
        if (pageDataSource.getQuery() == search) return

        pageDataSource.updateQuery(search, 0, 10)
    }

    fun postBuy(packages: Package) {
        _buy.value = packages
    }

    /**
     * Retry possible last paged request (ie: network issue)
     */
    fun refreshFailedRequest() = pageDataSource.getSource()?.retryFailedQuery()

    /**
     * Refreshes all list after an issue
     */
    fun refreshAllList() = pageDataSource.getSource()?.refresh()

    /**
     * Returns current search query
     */
    fun getCurrentQuery() = pageDataSource.getQuery()

    // UTILS ---

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(5)
        .setEnablePlaceholders(false)
        .setPageSize(5 * 2)
        .build()

}