package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Lesson

class LessonAdapter(private val lessonList: List<Lesson>, private val context: Context, private val onClick: OnItemClicked) : RecyclerView.Adapter<LessonAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.package_item, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemLesson = lessonList[position]

        val cover = itemLesson.lessonCover
        holder.packageCover.setImageURI(Uri.parse(cover))
        holder.packageName.text = itemLesson.lessonName
        holder.packageDesc.text = itemLesson.lessonDescription
        holder.packagePrice.text = itemLesson.lessonPrice.toString() + " Credits"
        holder.mRelativeLayout.setOnClickListener { view -> onClick.onItemClick(itemLesson) }
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    interface OnItemClicked {
        fun onItemClick(lesson: Lesson)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var packagePrice: TextView
        internal var packageDesc: TextView
        internal var packageName: TextView
        internal var packageCover: SimpleDraweeView
        internal var mRelativeLayout: RelativeLayout

        init {
            packagePrice = itemView.findViewById(R.id.package_price)
            packageDesc = itemView.findViewById(R.id.package_desc)
            packageName = itemView.findViewById(R.id.package_name)
            packageCover = itemView.findViewById(R.id.package_cover)
            mRelativeLayout = itemView.findViewById(R.id.relative_layout)
        }
    }
}

