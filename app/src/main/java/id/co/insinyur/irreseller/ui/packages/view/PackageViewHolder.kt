package id.co.insinyur.irreseller.ui.packages.view

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.model.Package
import kotlinx.android.synthetic.main.item_package.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class PackageViewHolder(private val parent: View, private val callback: PackageAdapter.OnClickListener): RecyclerView.ViewHolder(parent) {
    fun bindTo(packages: Package?) {

        packages?.let {
            itemView.cl_main.setOnClickListener { view ->
                val popupMenu = PopupMenu(parent.context, view)
                popupMenu.setOnMenuItemClickListener{ menuItem ->
                    if (menuItem.itemId == R.id.menu_buy_packet){
                        callback.onClickBuyPacket(R.id.menu_buy_packet, it)
                    }
                    true
                }
                popupMenu.inflate(R.menu.menu_package)
                popupMenu.show()
            }

            itemView.tv_icon.text = it.name[0].toString()
            itemView.tv_title.text = it.name
            itemView.tv_long_information.text = "${it.validity_value} ${it.validity_unit}"
            itemView.tv_pre_price.text = it.price.toString()
            itemView.tv_margin.text = it.margin.toString()
            itemView.tv_post_price.text = (it.price + it.margin).toString()

            try {
                val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                val result1: Date = df1.parse(it.created_at)

                val prettyTime = PrettyTime()
                val ago: String = prettyTime.format(result1)
                itemView.tv_date.text = ago
            } catch (e:Exception) {e.printStackTrace()
                itemView.tv_date.text = "Parsing error..."
            }
        }
    }
}