package id.co.insinyur.irreseller.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.Glide
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.api.NetworkState
import id.co.insinyur.irreseller.base.BaseFragment
import id.co.insinyur.irreseller.model.Transaction
import id.co.insinyur.irreseller.ui.transaction.view.TransactionAdapter
import id.co.insinyur.irreseller.utils.convertFilterToIndex
import id.co.insinyur.irreseller.utils.convertIndexToFilter
import id.co.insinyur.irreseller.utils.getQueryTextChangeStateFlow
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionFragment : BaseFragment(), TransactionAdapter.OnClickListener{

    private val viewModel: TransactionViewModel by viewModel()
    private lateinit var adapter: TransactionAdapter

    private fun configureRecyclerView(){
        adapter = TransactionAdapter(this)
        rv_content.adapter = adapter
    }

    private fun configureObservables() {
        viewModel.networkState?.observe(this, { adapter.updateNetworkState(it) })
        viewModel.transactions.observe(this, { adapter.submitList(it) })
        viewModel.changeProfile.observe(this, {
            showDialogNotification(R.string.profile_success_to_change, it.message)})
        viewModel.message.observe(this, { showMessage(it) })
    }

    private fun configureOnClick(){
        btn_refresh_empty_list.setOnClickListener { viewModel.refreshAllList() }
        fab_search.setOnClickListener { showDialogWithFilterItems { viewModel.refreshAllList() } }
    }

    private fun showMessage(m: String){
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
    }

    private fun showDialogNotification(title: Int, message: String){
        MaterialDialog(requireContext()).show {
            title(title)
            message(null, message)
            cancelable(false)
            positiveButton(R.string.ok) { return@positiveButton }
        }
    }

    private fun showDialogChangeProfile(transaction: Transaction) {
        var profile = transaction.radpackage?.`package`?.profile.toString() //radpackage?.users?.filter { l -> l.attribute.equals("user-profile", true) }
        MaterialDialog(requireContext()).show {
            title(R.string.change_profile)
            listItemsSingleChoice(R.array.profile) { _, index, _ ->
                val arr = resources.getStringArray(R.array.profile)
                if (arr[index].equals("game", true)) {
                    profile = arr[index]
                }
                viewModel.changeProfile(transaction.radpackage?.username.toString(), profile)
            }
            positiveButton { }
        }
    }

    private fun showDialogWithFilterItems(callback: () -> Unit) {
        MaterialDialog(this.activity!!).show {
            title(R.string.filter_popup_title)
            listItemsSingleChoice(
                    R.array.filters,
                    initialSelection = convertFilterToIndex(viewModel.getFilterWhenSearchingUsers())
            ) { _, index, _ ->
                viewModel.saveFilterWhenSearchingUsers(convertIndexToFilter(index))
            }
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
                    Glide.with(this).load(R.drawable.ic_directions_run_black_24dp).into(
                            imv_empty_list_image
                    )
                    tv_empty_list_title.text = getString(R.string.no_result_found)
                    imv_empty_list_image.visibility = View.VISIBLE
                    tv_empty_list_title.visibility = View.VISIBLE
                    btn_refresh_empty_list.visibility = View.GONE
                }
                NetworkState.FAILED -> {
                    Glide.with(this).load(R.drawable.ic_healing_black_24dp).into(
                            imv_empty_list_image
                    )
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
        return R.layout.fragment_transaction
    }

    override fun onClickRetry() {
        viewModel.refreshFailedRequest()
    }

    override fun whenListIsUpdated(size: Int, networkState: NetworkState?) {
        updateUIWhenLoading(size, networkState)
        updateUIWhenEmptyList(size, networkState)
    }

    override fun onClickChangePacket(idButton: Int, transaction: Transaction) {

        if (transaction.radpackage?.username == null) {
            Toast.makeText(
                    requireContext(),
                    R.string.in_transaction_connot_be_change_package,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }

        var valid = false
        transaction.radpackage.users?.forEach {
            if (it.attribute.equals("expiration", true)){
                Log.e("EXPIRATION", "FOUND")
                val format = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                val d = Date()
                // jika tanggal sekarang compare ke tanggal db > 0  maka tolak tanggal tersebut
                try {
                    val date = format.parse(it.value)
                    if (d <= date) { valid = true }
                } catch (e: ParseException) { e.printStackTrace() }
            }
        }
        if (!valid) {
            Toast.makeText(requireContext(), R.string.voucher_must_be_activated, Toast.LENGTH_SHORT).show()
            return
        }
        showDialogChangeProfile(transaction)
    }
}