package id.co.insinyur.irreseller.datasource

import id.co.insinyur.irreseller.api.PackageService

class PackageDataSource(private val service: PackageService) {
    suspend fun search(url: String, bearer: String, query: String, id: Int, perPage: Int) = service.fetch(url, bearer, query, id, perPage).await()

    suspend fun buy(url: String, bearer: String, idPackage: Int) = service.buy(url, bearer, idPackage).await()
}