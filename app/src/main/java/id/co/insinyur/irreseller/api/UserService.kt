package id.co.insinyur.irreseller.api

import id.co.insinyur.irreseller.model.Repository
import id.co.insinyur.irreseller.model.Result
import id.co.insinyur.irreseller.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("search/users")
    fun search(@Query("q") query: String,
               @Query("page") page: Int,
               @Query("per_page") perPage: Int,
               @Query("sort") sort: String
    ): Deferred<Result>

    @GET("users/{username}")
    fun getDetail(@Path("username") username: String): Deferred<User>

    @GET("users/{username}/repos")
    fun getRepos(@Path("username") username: String ): Deferred<List<Repository>>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String,
                     @Query("per_page") perPage: Int = 2): Deferred<List<User>>
}