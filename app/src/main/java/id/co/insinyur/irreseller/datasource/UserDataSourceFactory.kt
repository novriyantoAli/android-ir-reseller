package id.co.insinyur.irreseller.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.co.insinyur.irreseller.datasource.UserDataSource
import id.co.insinyur.irreseller.model.User
import id.co.insinyur.irreseller.repository.UserRepository
import kotlinx.coroutines.CoroutineScope

class UserDataSourceFactory(private val repository: UserRepository,
                            private var query: String = "",
                            private var sort: String = "",
                            private val scope: CoroutineScope): DataSource.Factory<Int, User>() {

    val source = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, User> {
        val source = UserDataSource(repository, query, sort, scope)
        this.source.postValue(source)
        return source
    }

    // --- PUBLIC API

    fun getQuery() = query

    fun getSource() = source.value

    fun updateQuery(query: String, sort: String){
        this.query = query
        this.sort = sort
        getSource()?.refresh()
    }
}