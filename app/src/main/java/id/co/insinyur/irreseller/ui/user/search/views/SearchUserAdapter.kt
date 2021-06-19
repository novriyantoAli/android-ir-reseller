//package id.co.insinyur.irreseller.ui.user.search.views
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.paging.PagedListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import id.co.insinyur.irreseller.R
//import id.co.insinyur.irreseller.api.NetworkState
//import id.co.insinyur.irreseller.model.User
//
//class SearchUserAdapter(private val callback: OnClickListener): PagedListAdapter<User, RecyclerView.ViewHolder>(
//    diffCallback
//) {
//
//    // FOR DATA ---
//    private var networkState: NetworkState? = null
//    interface OnClickListener {
//        fun onClickRetry()
//        fun whenListIsUpdated(size: Int, networkState: NetworkState?)
//    }
//
//    // OVERRIDE ---
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
//        return when (viewType) {
//            R.layout.item_transaction -> SearchUserViewHolder(
//                view
//            )
//            R.layout.item_network_state -> SearchUserNetworkStateViewHolder(
//                view
//            )
//            else -> throw IllegalArgumentException("Unknown view type $viewType")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (getItemViewType(position)) {
//            R.layout.item_transaction -> (holder as SearchUserViewHolder).bindTo(getItem(position))
//            R.layout.item_network_state -> (holder as SearchUserNetworkStateViewHolder).bindTo(networkState, callback)
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (hasExtraRow() && position == itemCount - 1) {
//            R.layout.item_network_state
//        } else {
//            R.layout.item_transaction
//        }
//    }
//
//    override fun getItemCount(): Int {
//        this.callback.whenListIsUpdated(super.getItemCount(), this.networkState)
//        return super.getItemCount()
//    }
//
//    // UTILS ---
//    private fun hasExtraRow() = networkState != null && networkState != NetworkState.SUCCESS
//
//    // PUBLIC API ---
//    fun updateNetworkState(newNetworkState: NetworkState?) {
//        val previousState = this.networkState
//        val hadExtraRow = hasExtraRow()
//        this.networkState = newNetworkState
//        val hasExtraRow = hasExtraRow()
//        if (hadExtraRow != hasExtraRow) {
//            if (hadExtraRow) {
//                notifyItemRemoved(super.getItemCount())
//            } else {
//                notifyItemInserted(super.getItemCount())
//            }
//        } else if (hasExtraRow && previousState != newNetworkState) {
//            notifyItemChanged(itemCount - 1)
//        }
//    }
//
//    companion object {
//        private val diffCallback = object : DiffUtil.ItemCallback<User>() {
//            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
//            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
//        }
//    }
//}