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

class LessonByPackageDetailAdapter(private val lessonList: List<Lesson>, private val context: Context, private val onClick: OnItemClicked) : RecyclerView.Adapter<LessonByPackageDetailAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.lesson_package_detail_item, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemLesson = lessonList[position]
        val cover = itemLesson.lessonCover
        holder.lessonCover.setImageURI(Uri.parse(cover))
        holder.lessonName.text = itemLesson.lessonName
        holder.lessonDesc.text = itemLesson.lessonDescription
        holder.lessonDuration.text = itemLesson.classMin.toString()
        holder.lessonCredit.text = itemLesson.lessonPrice.toString()
        holder.mRelativeLayout.setOnClickListener { view -> onClick.onItemClick(itemLesson) }
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    interface OnItemClicked {
        fun onItemClick(lesson: Lesson)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var lessonCredit: TextView
        internal var lessonDesc: TextView
        internal var lessonName: TextView
        internal var lessonDuration: TextView
        internal var lessonCover: SimpleDraweeView
        internal var mRelativeLayout: RelativeLayout

        init {
            lessonCredit = itemView.findViewById(R.id.credit)
            lessonDesc = itemView.findViewById(R.id.lesson_desc)
            lessonName = itemView.findViewById(R.id.lesson_name)
            lessonCover = itemView.findViewById(R.id.lesson_cover)
            mRelativeLayout = itemView.findViewById(R.id.relative_layout)
            lessonDuration = itemView.findViewById(R.id.lesson_duration)
        }
    }
}