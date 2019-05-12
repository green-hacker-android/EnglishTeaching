package inc.osbay.android.tutorroom.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.Booking
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.ui.activity.ClassRoomActivity


private class BookingScheduleAdapter(private val mSelectedBookingList: List<Booking>, private val mServerRequestManager: ServerRequestManager,
                                     private val context: Context, private val onClick: OnItemClicked) : RecyclerView.Adapter<BookingScheduleAdapter.MyViewHolder>() {
    private val tutorAdapter: TutorAdapter
    private val mDBAdapter: DBAdapter
    private var mSelectedBooking: Booking? = null

    init {
        tutorAdapter = TutorAdapter(context)
        mDBAdapter = DBAdapter(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.booked_class_item,
                parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val booking = mSelectedBookingList[i]
        if (booking.bookingType == CommonConstant.singleLessonType)
            holder.horiView.background = context.resources.getDrawable(R.drawable.single_class_view_bg)
        else if (booking.bookingType == CommonConstant.Trial)
            holder.horiView.background = context.resources.getDrawable(R.drawable.trial_view_bg)
        try {
            holder.stateDateTV.text = LGCUtil.changeDateFormat(booking.startDate!!, LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_TIME_NO_SEC)
            holder.endDateTV.text = LGCUtil.changeDateFormat(booking.endDate!!, LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_TIME_NO_SEC)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val mTutor = tutorAdapter.getTutorById(booking.tutorId!!)
        holder.tutorNameTV.text = mTutor!!.name
        holder.tutorImg.setImageURI(Uri.parse(mTutor.avatar))

        val lessonCount = mDBAdapter.getLessonCountByID(booking.lessonId)
        if (lessonCount == 0) {
            holder.lessonTV.text = booking.lessonId
        } else {
            val lesson = mDBAdapter.getLessonByID(booking.lessonId!!)
            holder.lessonTV.text = lesson.lessonName
            holder.lessonImg.setImageURI(Uri.parse(lesson.lessonCover))
        }

        holder.startTV.setOnClickListener { view ->
            mSelectedBooking = booking
            System.gc()
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.sd_network_notice))
                    .setMessage(context.getString(R.string.sd_network_msg))
                    .setPositiveButton(context.getString(R.string.sd_network_continue)) { dialogInterface, i1 ->
                        val intent = Intent(context, ClassRoomActivity::class.java)
                        intent.putExtra(ClassRoomActivity.EXTRA_BOOKING, mSelectedBooking)
                        context.startActivity(intent)
                    }
                    .setNegativeButton(context.getString(R.string.cr_leave_room_cancel), null)
                    .create()
                    .show()
        }

        mServerRequestManager.checkStartClass(booking.bookingId!!, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                if (response!!.code == 1) {
                    try {
                        val jsonObject = JSONObject(response.dataSt)
                        val isStart = jsonObject.getString("is_start")
                        if (isStart.equals("false", ignoreCase = true)) {
                            holder.startTV.isClickable = false
                            holder.startTV.background = context.resources.getDrawable(R.drawable.btn_bg_rounded_green_disable)
                        } else {
                            holder.startTV.isClickable = true
                            holder.startTV.background = context.resources.getDrawable(R.drawable.btn_bg_rounded_green)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onError(err: ServerError) {}
        })
    }

    override fun getItemCount(): Int {
        return mSelectedBookingList.size
    }

    interface OnItemClicked {
        fun onItemClick(lessonID: String)
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stateDateTV: TextView
        val endDateTV: TextView
        val tutorNameTV: TextView
        val lessonTV: TextView
        val startTV: TextView
        val tutorImg: SimpleDraweeView
        val lessonImg: SimpleDraweeView
        val horiView: View

        init {
            stateDateTV = view.findViewById(R.id.start_time)
            endDateTV = view.findViewById(R.id.end_time)
            tutorNameTV = view.findViewById(R.id.tutor_name_tv)
            lessonTV = view.findViewById(R.id.lesson_tv)
            startTV = view.findViewById(R.id.start_class)
            tutorImg = view.findViewById(R.id.sdv_tutor_photo)
            lessonImg = view.findViewById(R.id.sdv_lesson_photo)
            horiView = view.findViewById(R.id.view_pink)
        }
    }
}
