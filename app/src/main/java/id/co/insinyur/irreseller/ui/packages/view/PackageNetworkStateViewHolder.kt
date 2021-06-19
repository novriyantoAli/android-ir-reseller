package id.co.insinyur.irreseller.ui.packages.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.co.insinyur.irreseller.api.NetworkState

import kotlinx.android.synthetic.main.item_network_state.view.item_search_user_network_state_progress_bar as progressBar
import kotlinx.android.synthetic.main.item_network_state.view.item_search_user_network_state_button as retryButton
import kotlinx.android.synthetic.main.item_network_state.view.item_search_user_network_state_title as retryTitle

class PackageNetworkStateViewHolder(parent: View): RecyclerView.ViewHolder(parent){
    fun bindTo(networkState: NetworkState?, callback: PackageAdapter.OnClickListener){
        hideViews()
        setVisibleRightViews(networkState)
        itemView.retryButton.setOnClickListener{ callback.onClickRetry() }
    }

    private fun hideViews() {
        itemView.retryButton.visibility = View.GONE
        itemView.retryTitle.visibility = View.GONE
        itemView.progressBar.visibility = View.GONE
    }

    private fun setVisibleRightViews(networkState: NetworkState?) {
        when (networkState) {
            NetworkState.FAILED -> {
                itemView.retryButton.visibility = View.VISIBLE
                itemView.retryTitle.visibility = View.VISIBLE
            }
            NetworkState.RUNNING -> {
                itemView.progressBar.visibility = View.VISIBLE
            }
        }
    }
}