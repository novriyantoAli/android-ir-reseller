package id.co.insinyur.irreseller.ui.transaction.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope

class TransactionPageDataSourceFactory(
    private val repository: TransactionRepository,
    private val url: String,
    private var query: String = "",
    private var id: Int = 0,
    private var perPage: Int = 10,
    private val scope: CoroutineScope): DataSource.Factory<Int, Transaction>() {

    val source = MutableLiveData<TransactionPageDataSource>()

    override fun create(): DataSource<Int, Transaction> {
        val source = TransactionPageDataSource(repository, url, query, id, perPage, scope)
        this.source.postValue(source)
        return source
    }

    fun getQuery() = query

    fun getSource() = source.value

    fun updateQuery(query: String, id: Int, perPage: Int) {
        this.query = query
        this.id = id
        this.perPage = perPage

        getSource()?.refresh()
    }
}

//class UserDataSourceFactory(private val repository: UserRepository,
//                            private var query: String = "",
//                            private var sort: String = "",
//                            private val scope: CoroutineScope
//): DataSource.Factory<Int, User>() {
//
//    val source = MutableLiveData<UserDataSource>()
//
//    override fun create(): DataSource<Int, User> {
//        val source = UserDataSource(repository, query, sort, scope)
//        this.source.postValue(source)
//        return source
//    }
//
//    // --- PUBLIC API
//
//    fun getQuery() = query
//
//    fun getSource() = source.value
//
//    fun updateQuery(query: String, sort: String){
//        this.query = query
//        this.sort = sort
//        getSource()?.refresh()
//    }
//}