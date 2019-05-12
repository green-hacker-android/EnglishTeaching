package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Notification

class NotificationAdapter(private val notiList: List<Notification>, private val context: Context, private val onClick: OnItemClicked) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notiList[position]
        holder.notiCover.setImageDrawable(context.resources.getDrawable(R.mipmap.ic_chat_support))
        holder.notiTitle.text = notification.title
        if (notification.status == 1) {
            holder.newLb.visibility = View.VISIBLE
            holder.notiTitle.setTypeface(null, Typeface.BOLD)
        } else {
            holder.newLb.visibility = View.GONE
            holder.notiTitle.setTypeface(null, Typeface.NORMAL)
        }
        holder.newRL.setOnClickListener { view -> onClick.onItemClick(notification.notiId) }
    }

    interface OnItemClicked {
        fun onItemClick(notiID: String?)
    }

    override fun getItemCount(): Int {
        return notiList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var notiTitle: TextView
        var newLb: TextView
        var notiCover: SimpleDraweeView
        var newRL: RelativeLayout

        init {
            notiTitle = itemView.findViewById(R.id.noti_title)
            newLb = itemView.findViewById(R.id.new_lb)
            notiCover = itemView.findViewById(R.id.noti_img)
            newRL = itemView.findViewById(R.id.noti_rl)
        }
    }
}
