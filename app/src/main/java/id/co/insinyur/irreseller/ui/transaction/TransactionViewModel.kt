package id.co.insinyur.irreseller.ui.transaction

import android.app.Application
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.base.BaseViewModel
import id.co.insinyur.irreseller.model.Filters
import id.co.insinyur.irreseller.model.MessageResponse
import id.co.insinyur.irreseller.model.Package
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.repository.TransactionRepository
import id.co.insinyur.irreseller.storage.SharedPrefsManager
import id.co.insinyur.irreseller.ui.transaction.datasource.TransactionPageDataSourceFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class TransactionViewModel(val repository: TransactionRepository, private val app: Application, private val sharedPrefsManager: SharedPrefsManager): BaseViewModel() {
    // FOR DATA ---
    private val pageDataSource = TransactionPageDataSourceFactory(repository = repository, scope = ioScope, url = app.resources.getStringArray(R.array.urls)[4])

    // OBSERVABLES ---
    val transactions = LivePagedListBuilder(pageDataSource, pagedListConfig()).build()
    val networkState: LiveData<NetworkState>? = switchMap(pageDataSource.source) { it.getNetworkState() }

    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    private val _profile = MutableLiveData<List<String>>()
    val changeProfile: LiveData<MessageResponse> = _profile.switchMap { pac ->
        liveData {
            repository.postChangeProfile(voucher = pac[0], profile = pac[1])
                    .onStart {  }
                    .catch { _message.value = it.localizedMessage }
                    .onCompletion {  }
                    .collect {
                        emit(it)
                    }
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

    fun changeProfile(voucher: String, profile: String){
        _profile.value = listOf(voucher, profile)
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
     * Returns filter [Filters.ResultSearchUsers] used to sort "search" request
//     */
    fun getFilterWhenSearchingUsers() = sharedPrefsManager.getFilterWhenSearchingUsers()

    /**
     * Saves filter [Filters.ResultSearchUsers] used to sort "search" request
     */
    fun saveFilterWhenSearchingUsers(filter: Filters.ResultSearchUsers) =
        sharedPrefsManager.saveFilterWhenSearchingUsers(filter)

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