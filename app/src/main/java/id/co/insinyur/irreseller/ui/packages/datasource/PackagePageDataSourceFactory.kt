package id.co.insinyur.irreseller.ui.packages.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import id.co.insinyur.irreseller.model.Package
import id.co.insinyur.irreseller.repository.PackageRepository
import kotlinx.coroutines.CoroutineScope

class PackagePageDataSourceFactory(
    private val repository: PackageRepository,
    private val path: String = "",
    private var query: String = "",
    private var id: Int = 0,
    private var perPage: Int = 10,
    private val scope: CoroutineScope
): DataSource.Factory<Int, Package>() {

    val source = MutableLiveData<PackagePageDataSource>()

    override fun create(): DataSource<Int, Package> {
        val source = PackagePageDataSource(repository,path, query, id, perPage, scope)
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
