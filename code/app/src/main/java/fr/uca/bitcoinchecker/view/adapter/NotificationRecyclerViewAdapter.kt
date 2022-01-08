package fr.uca.bitcoinchecker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.iut.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.R

class NotificationRecyclerViewAdapter(private val context : Context, private val dataSet: List<NotificationItem>) : RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val content : TextView
        val creationDate : TextView
        val icon : ImageView

        init{
            content = view.findViewById(R.id.contentNotification)
            creationDate = view.findViewById(R.id.creationDateNotification)
            icon = view.findViewById(R.id.iconNotification)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notification_list_row, viewGroup, false)

        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: NotificationViewHolder, position: Int) {
        if(dataSet[position].variation == NotificationItem.Variation.UP) {
            viewHolder.content.text = String.format("%s %d$", context.getString(R.string.upTo)  ,dataSet[position].value)
            viewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
        }
        else{
            viewHolder.content.text = String.format("%s %d$", context.getString(R.string.downTo)  ,dataSet[position].value)
            viewHolder.icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }
        viewHolder.creationDate.text = dataSet[position].creationDate.toString()

    }

    override fun getItemCount() = dataSet.size
}