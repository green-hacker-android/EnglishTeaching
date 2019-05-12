package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Lesson

class LessonBookingAdapter(private val lessonList: List<Lesson>, private var lessonID: Int, private val context: Context, private val onClick: OnItemClicked) : RecyclerView.Adapter<LessonBookingAdapter.MyViewHolder>() {
    private var row_index = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.lesson_booking_item, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemLesson = lessonList[position]

        holder.lessonTitleTV.text = itemLesson.lessonName
        holder.mRelativeLayout.setOnClickListener { view ->
            lessonID = 0
            row_index = position
            notifyDataSetChanged()
            onClick.onItemClick(lessonList[position].lessonId)
        }
        if (row_index == position || lessonID == Integer.parseInt(itemLesson.lessonId!!)) {
            holder.mRelativeLayout.setBackgroundColor(context.resources.getColor(R.color.pink))
            holder.lessonTitleTV.setTextColor(context.resources.getColor(R.color.white))
        } else if (row_index != position) {
            holder.mRelativeLayout.setBackgroundColor(context.resources.getColor(R.color.white))
            holder.lessonTitleTV.setTextColor(context.resources.getColor(R.color.colorAccent))
        }
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    interface OnItemClicked {
        fun onItemClick(lessonID: String?)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonTitleTV: TextView
        val mRelativeLayout: RelativeLayout

        init {
            mRelativeLayout = view.findViewById(R.id.lesson_prl)
            lessonTitleTV = view.findViewById(R.id.tv_title)
        }
    }
}

