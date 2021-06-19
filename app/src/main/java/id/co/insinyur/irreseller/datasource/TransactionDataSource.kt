package id.co.insinyur.irreseller.datasource

import id.co.insinyur.irreseller.api.TransactionService

class TransactionDataSource(private val service: TransactionService) {
    suspend fun search(url: String, bearer: String, query: String, id: Int, perPage: Int) = service.fetch(url, bearer, query, id, perPage).await()
    suspend fun changePackage(bearer: String, voucher:String, profile: String) = service.changePackage(bearer, voucher, profile).await()
}

//class UserRepository(private val service: UserService) {
//
//    private suspend fun search(query: String, page: Int, perPage: Int, sort: String) = service.search(query, page, perPage, sort).await()
//
//    private suspend fun getDetail(login: String) = service.getDetail(login).await()
//
//    private suspend fun getRepos(login: String) = service.getRepos(login).await()
//
//    private suspend fun getFollowers(login: String) = service.getFollowers(login).await()
//
//    suspend fun searchUsersWithPagination(query: String, page: Int, perPage: Int, sort: String): List<User> {
//        if (query.isEmpty()) return listOf()
//
//        val users = mutableListOf<User>()
//        val request = search(query, page, perPage, sort) // Search by name
//        request.items.forEach {
//            val user = getDetail(it.login) // Fetch detail for each user
//            val repositories = getRepos(user.login) // Fetch all repos for each user
//            val followers = getFollowers(user.login) // Fetch all followers for each user
//
//            user.totalStars =  repositories.map { it.numberStars }.sum()
//            user.followers = if (followers.isNotEmpty()) followers else listOf()
//
//            users.add(user)
//        }
//        return users
//    }
//}