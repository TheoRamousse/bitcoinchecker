package fr.uca.bitcoinchecker.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.uca.bitcoinchecker.R
import fr.uca.bitcoinchecker.model.NotificationItem
import fr.uca.bitcoinchecker.view.activity.ViewNotificationActivity

class NotificationRecyclerViewAdapter(private val context : Context, private val containerName: String, private val cryptoIdInApi: String, private val mainActivity: Callback) : RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val content : TextView = view.findViewById(R.id.contentNotification)
        val creationDate : TextView = view.findViewById(R.id.creationDateNotification)
        val icon : ImageView = view.findViewById(R.id.iconNotification)
        val rowContainer: LinearLayout = view.findViewById(R.id.row_container)
        val buttonDelete: TextView = view.findViewById(R.id.button_delete)

    }

    private var dataSet: List<NotificationItem> = emptyList()

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
        viewHolder.creationDate.text = DateFormat.getDateFormat(context).format(dataSet[position].creationDate!!)

        when(dataSet[position].importance){
            NotificationItem.NotificationImportance.LOW ->
                viewHolder.rowContainer.setBackgroundColor(context.resources.getColor(R.color.low))
            NotificationItem.NotificationImportance.MEDIUM ->
                viewHolder.rowContainer.setBackgroundColor(context.resources.getColor(R.color.medium))
            NotificationItem.NotificationImportance.HIGH ->
                viewHolder.rowContainer.setBackgroundColor(context.resources.getColor(R.color.high))

        }

        viewHolder.rowContainer.setOnClickListener {
            context.startActivity(ViewNotificationActivity.getIntent(context, dataSet[position].id!!, cryptoIdInApi, containerName))
        }

        viewHolder.buttonDelete.setOnClickListener {
            mainActivity.receiveRemove(dataSet[position])
        }

    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(listUpdated : List<NotificationItem>){
        dataSet = listUpdated
        notifyDataSetChanged()
    }


    public interface Callback {
        fun receiveRemove(notification: NotificationItem)
    }
}