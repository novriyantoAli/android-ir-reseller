package id.co.insinyur.irreseller.ui.transaction.view

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.model.Transaction
import kotlinx.android.synthetic.main.item_transaction.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewHolder(private val parent: View, private val callback: TransactionAdapter.OnClickListener): RecyclerView.ViewHolder(parent) {
    fun bindTo(transaction: Transaction?) {

        transaction?.let {
            itemView.cl_main.setOnClickListener { view ->
                val popupMenu = PopupMenu(parent.context, view)
                popupMenu.setOnMenuItemClickListener{ menuItem ->
                    if (menuItem.itemId == R.id.menu_change_packet){
                        callback.onClickChangePacket(R.id.menu_change_packet, it)
                    }
                    true
                }
                popupMenu.inflate(R.menu.menu_transaction)
                popupMenu.show()
            }

            itemView.tv_profile.text = if (it.radpackage?.username == null) it.status[0].toString()
            else it.radpackage.username[0].toString()

            itemView.tv_title.text = if (it.radpackage?.username == null) "Transaction IN"
            else it.radpackage.username

            itemView.tv_money.text = if (it.radpackage?.username == null) "Balance IN = ${it.value}"
            else "[ ${it.radpackage.`package`.validity_value} ${it.radpackage.`package`.validity_unit} ], Price: ${it.radpackage.`package`.price}, Margin: ${it.radpackage.`package`.margin}, Total: ${it.value}"

            itemView.tv_timeout.text = if (it.radpackage?.users != null && it.radpackage.users.isNotEmpty()) {
                var stringReturn = "Belum diaktifkan"
                it.radpackage.users.forEach { us ->
                    if (us.attribute.equals("expiration", true)) {
                        stringReturn = us.value
                    }
                }
                stringReturn
            } else "Tidak berlaku"


            try {
                val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                val result1: Date = df1.parse(it.created_at)

                val prettyTime = PrettyTime()
                val ago: String = prettyTime.format(result1)
                itemView.tv_clock.text = ago
            } catch (e:Exception) {e.printStackTrace()
                itemView.tv_clock.text = "Parsing error..."
            }
        }
    }
}

//class SearchUserViewHolder(parent: View): RecyclerView.ViewHolder(parent) {
//
//    // PUBLIC API ---
//    fun bindTo(user: User?){
//        user?.let {
//            loadImage(it.avatarUrl, itemView.image)
//            val firstFollower: User? = try { it.followers[0] } catch (e: Exception) { null }
//            val secondFollower: User? = try { it.followers[1] } catch (e: Exception) { null }
//            firstFollower?.let { loadImage(it.avatarUrl, itemView.followerImage) }
//            secondFollower?.let { loadImage(it.avatarUrl, itemView.followerImageBis) }
//            itemView.title.text = it.login.capitalize()
//            itemView.repositories.text = "${it.totalStars} - ${it.totalRepos} ${itemView.context.getString(
//                R.string.repositories)}"
//            itemView.followerName.text = firstFollower?.login?.capitalize()
//            itemView.followerCount.text = "+${it.totalFollowers - 1}"
//        }
//    }
//
//    // UTILS ---
//    private fun loadImage(url: String, imageView: ImageView) {
//        Glide.with(itemView.context)
//            .load(url)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .apply(RequestOptions.circleCropTransform())
//            .into(imageView)
//    }
//}