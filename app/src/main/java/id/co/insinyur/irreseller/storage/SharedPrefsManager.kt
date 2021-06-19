package id.co.insinyur.irreseller.storage

import android.content.Context
import android.content.SharedPreferences
import id.co.insinyur.irreseller.extensions.setValue
import id.co.insinyur.irreseller.model.Filters
import java.lang.IllegalStateException

class SharedPrefsManager(private val context: Context) {

    private fun get(): SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    /**
     * Returns [Filters.ResultSearchUsers] saved in [SharedPreferences]
     * This filter will be used to sort "search" queries
     */
    fun getFilterWhenSearchingUsers(): Filters.ResultSearchUsers {
        return when (get().getString(KEY_FILTERS, Filters.ResultSearchUsers.BY_SCORE.value)) {
            Filters.ResultSearchUsers.BY_SCORE.value -> Filters.ResultSearchUsers.BY_SCORE
            Filters.ResultSearchUsers.BY_REPOS.value -> Filters.ResultSearchUsers.BY_REPOS
            Filters.ResultSearchUsers.BY_FOLLOWERS.value -> Filters.ResultSearchUsers.BY_FOLLOWERS
            else -> throw IllegalStateException("Filter not recognized")
        }
    }

    /**
     * Saves [Filters.ResultSearchUsers] in [SharedPreferences]
     * This filter will be used to sort "search" queries
     */
    fun saveFilterWhenSearchingUsers(filters: Filters.ResultSearchUsers) {
        get().setValue(KEY_FILTERS, filters.value)
    }

    companion object {
        private const val SP_NAME = "GithubAppPrefs"
        private const val KEY_FILTERS = "KEY_FILTERS"
    }
}