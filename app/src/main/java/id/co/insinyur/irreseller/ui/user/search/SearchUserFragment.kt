package id.co.insinyur.irreseller.ui.user.search


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.Glide
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.base.BaseFragment
import id.co.insinyur.irreseller.extensions.onQueryTextChange
//import id.co.insinyur.irreseller.ui.user.search.views.SearchUserAdapter
import id.co.insinyur.irreseller.utils.convertFilterToIndex
import id.co.insinyur.irreseller.utils.convertIndexToFilter
import kotlinx.android.synthetic.main.fragment_search_user.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchUserFragment : BaseFragment(){//, SearchUserAdapter.OnClickListener {

    // FOR DATA ---
    private val viewModel: SearchUserViewModel by viewModel()
//    private lateinit var adapter: SearchUserAdapter

    // OVERRIDE ---
    override fun getLayoutId(): Int = R.layout.fragment_search_user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        configureRecyclerView()
//        configureObservables()
        configureOnClick()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        configureMenu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // ACTION ---
//    override fun onClickRetry() {
//        viewModel.refreshFailedRequest()
//    }
//
//    override fun whenListIsUpdated(size: Int, networkState: NetworkState?) {
//        updateUIWhenLoading(size, networkState)
//        updateUIWhenEmptyList(size, networkState)
//    }

    private fun configureOnClick(){
        btn_refresh_empty_list.setOnClickListener { viewModel.refreshAllList() }
        fab_search.setOnClickListener { showDialogWithFilterItems { viewModel.refreshAllList() } }
    }

    // CONFIGURATION ---
    private fun configureMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.action_search)
        val possibleExistingQuery = viewModel.getCurrentQuery()
        val searchView = searchMenuItem.actionView as SearchView
        if (possibleExistingQuery != null && possibleExistingQuery.isNotEmpty()) {
            searchMenuItem.expandActionView()
            searchView.setQuery(possibleExistingQuery, false)
            searchView.clearFocus()
        }
        searchView.onQueryTextChange {
            viewModel.fetchUsersByName(it)
        }
    }

//    private fun configureRecyclerView(){
////        adapter = SearchUserAdapter(this)
//        rv_content.adapter = adapter
//    }

//    private fun configureObservables() {
//        viewModel.networkState?.observe(this, { adapter.updateNetworkState(it) })
//        viewModel.users.observe(this, { adapter.submitList(it) })
//    }

    // UPDATE UI ----
    private fun updateUIWhenEmptyList(size: Int, networkState: NetworkState?) {
        imv_empty_list_image.visibility = View.GONE
        btn_refresh_empty_list.visibility = View.GONE
        tv_empty_list_title.visibility = View.GONE
        if (size == 0) {
            when (networkState) {
                NetworkState.SUCCESS -> {
                    Glide.with(this).load(R.drawable.ic_directions_run_black_24dp).into(imv_empty_list_image)
                    tv_empty_list_title.text = getString(R.string.no_result_found)
                    imv_empty_list_image.visibility = View.VISIBLE
                    tv_empty_list_title.visibility = View.VISIBLE
                    btn_refresh_empty_list.visibility = View.GONE
                }
                NetworkState.FAILED -> {
                    Glide.with(this).load(R.drawable.ic_healing_black_24dp).into(imv_empty_list_image)
                    tv_empty_list_title.text = getString(R.string.technical_error)
                    imv_empty_list_image.visibility = View.VISIBLE
                    tv_empty_list_title.visibility = View.VISIBLE
                    btn_refresh_empty_list.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUIWhenLoading(size: Int, networkState: NetworkState?) {
        pb_loading.visibility = if (size == 0 && networkState == NetworkState.RUNNING) View.VISIBLE else View.GONE
    }

    private fun showDialogWithFilterItems(callback: () -> Unit) {
        MaterialDialog(this.activity!!).show {
            title(R.string.filter_popup_title)
            listItemsSingleChoice(
                R.array.filters,
                initialSelection = convertFilterToIndex(viewModel.getFilterWhenSearchingUsers())) { _, index, _ ->
                viewModel.saveFilterWhenSearchingUsers(convertIndexToFilter(index))
            }
            positiveButton {
                callback()
            }
        }
    }
}
