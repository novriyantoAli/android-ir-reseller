package id.co.insinyur.irreseller.ui.packages

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.base.BaseFragment
import id.co.insinyur.irreseller.model.Package
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.ui.packages.view.PackageAdapter
import id.co.insinyur.irreseller.utils.getQueryTextChangeStateFlow
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 * Use the [PackageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PackageFragment : BaseFragment(), PackageAdapter.OnClickListener{

    private val viewModel: PackageViewModel by viewModel()
    private lateinit var adapter: PackageAdapter

    private fun configureRecyclerView(){
        adapter = PackageAdapter(this)
        rv_content.adapter = adapter
    }

    private fun configureObservables() {
        viewModel.networkState?.observe(this, { adapter.updateNetworkState(it) })
        viewModel.packages.observe(this, { adapter.submitList(it) })
        viewModel.buy.observe(requireActivity(), { if (it != null) { showDialogWithTransaction(it) } })
    }

    private fun configureOnClick(){
        btn_refresh_empty_list.setOnClickListener { viewModel.refreshAllList() }
        fab_search.setOnClickListener { showDialogWithFilterItems { viewModel.refreshAllList() } }
    }

    private fun showDialogWithTransaction(t: Transaction) {
        val price = t.radpackage?.`package`?.price ?: 0
        val margin = t.radpackage?.`package`?.margin ?: 0
        val message = "Voucher: ${t.radpackage?.username}\nPrice: ${price + margin}\nReseller: ${t.reseller.username}"
        MaterialDialog(requireContext()).show {
            title(R.string.success_buy_package)
            message(null, message)
            cancelable(false)
            negativeButton (R.string.share){
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            positiveButton (R.string.ok){ return@positiveButton }
        }
    }

    private fun showDialogWithConfirmBuy(p: Package) {
        MaterialDialog(requireContext()).show {
            title(null, p.name)
            message(R.string.are_you_sure_to_buy_this_package)
            cancelable(false)
            negativeButton(R.string.cancels) {
                return@negativeButton
            }
            positiveButton (R.string.buy){
                viewModel.postBuy(p)
            }
        }
    }

    private fun showDialogWithFilterItems(callback: () -> Unit) {
        MaterialDialog(this.activity!!).show {
            title(R.string.filter_popup_title)
//            listItemsSingleChoice(R.array.filters, initialSelection = convertFilterToIndex(viewModel.getFilterWhenSearchingUsers())) { _, index, _ ->
//                viewModel.saveFilterWhenSearchingUsers(convertIndexToFilter(index))
//            }
            positiveButton {
                callback()
            }
        }
    }

    private fun updateUIWhenLoading(size: Int, networkState: NetworkState?) {
        pb_loading.visibility = if (size == 0 && networkState == NetworkState.RUNNING) View.VISIBLE else View.GONE
    }

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

    // CONFIGURATION ---
    private fun configureMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.action_search)
        val possibleExistingQuery = viewModel.getCurrentQuery()
        val searchView = searchMenuItem.actionView as SearchView
        if (possibleExistingQuery.isNotEmpty()) {
            searchMenuItem.expandActionView()
            searchView.setQuery(possibleExistingQuery, false)
            searchView.clearFocus()
        }
//        searchView.onQueryTextChange {
//            viewModel.fetch(it)
//        }

        CoroutineScope(Dispatchers.Main).launch {
            searchView.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query -> return@filter query.isNotEmpty() }
                .distinctUntilChanged()
                .catch { e -> e.printStackTrace() }
                .flowOn(Dispatchers.Default)
                .collect { viewModel.fetch(it) }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        configureRecyclerView()
        configureObservables()
        configureOnClick()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        configureMenu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_package
    }

    override fun onClickRetry() {
        viewModel.refreshFailedRequest()
    }

    override fun whenListIsUpdated(size: Int, networkState: NetworkState?) {
        updateUIWhenLoading(size, networkState)
        updateUIWhenEmptyList(size, networkState)
    }

    override fun onClickBuyPacket(idButton: Int, packages: Package) {
        showDialogWithConfirmBuy(packages)
    }
}