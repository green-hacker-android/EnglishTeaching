package inc.osbay.android.tutorroom.ui.fragment

import android.app.Dialog
import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.view.SimpleDraweeView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.text.ParseException
import java.util.ArrayList
import java.util.Objects
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.AvailableTutor
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.FileDownloader
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.ui.activity.MainActivity
import inc.osbay.android.tutorroom.utils.CommonUtil
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class SingleBookingChooseTutorFragment : BackHandledFragment() {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.tutor_rv)
    internal var tutorRV: RecyclerView? = null
    @BindView(R.id.no_data)
    internal var noDataTV: TextView? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var mTutorListAdapter: AvailableTutorListAdapter? = null
    private val availableTutorList = ArrayList<AvailableTutor>()
    private var lessonID: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var accountID: String? = null
    private var source: String? = null
    private var tutorID: String? = "0"
    private var lessonType: String? = null
    private var mTimer: Timer? = null
    private var mTutorList: List<Tutor>? = null
    private val avaiTutorList = ArrayList<Tutor>()
    private var isPlaying: Boolean = false
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = arguments.getString("source")
        lessonID = arguments.getString("lesson_id")
        startDate = arguments.getString("start_date")
        endDate = arguments.getString("end_date")
        lessonType = arguments.getString("lesson_type")
        if (source == TutorInfoFragment::class.java!!.getSimpleName()) {
            tutorID = arguments.getString(TutorInfoFragment.EXTRA_TUTOR_ID)
        }
        val sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        accountID = sharedPreferenceData.getInt("account_id").toString()
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
        mTimer = Timer()
        val tutorDbAdapter = TutorAdapter(activity)
        mTutorList = tutorDbAdapter.allTutor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_single_booking_choose_tutor, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        try {
            mServerRequestManager!!.getAvailableTutorByTime(LGCUtil.convertToUTC(startDate!!), object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    progressDialog.dismiss()
                    if (result!!.code == 1)
                    //For Success Situation
                    {
                        if (activity != null) {
                            availableTutorList.clear()
                            avaiTutorList.clear()
                            try {
                                val dataObject = JSONObject(result.dataSt)
                                val fullTimeTutorJsonArray = dataObject.getJSONArray("tutor_fulltime")
                                val partTimeTutorJsonArray = dataObject.getJSONArray("tutor_parttime")
                                for (i in 0 until fullTimeTutorJsonArray.length()) {
                                    val tutor = AvailableTutor(fullTimeTutorJsonArray.getJSONObject(i))
                                    availableTutorList.add(tutor)
                                }
                                for (j in 0 until partTimeTutorJsonArray.length()) {
                                    val tutor = AvailableTutor(partTimeTutorJsonArray.getJSONObject(j))
                                    availableTutorList.add(tutor)
                                }

                            } catch (je: JSONException) {
                                Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je)
                            }

                        }

                        for (i in mTutorList!!.indices) {
                            for (j in availableTutorList.indices) {
                                if (availableTutorList[j].tutorId!!.equals(mTutorList!![i].tutorId!!, ignoreCase = true)) {
                                    avaiTutorList.add(mTutorList!![i])
                                }
                            }
                        }
                        /*mTutorListAdapter = new AvailableTutorListAdapter(lessonID, startDate, endDate, tutorID, lessonType,
                                availableTutorList, getActivity(), SingleBookingChooseTutorFragment.class.getSimpleName());*/
                        mTutorListAdapter = AvailableTutorListAdapter()
                        val mLayoutManager = LinearLayoutManager(activity)
                        tutorRV!!.layoutManager = mLayoutManager
                        tutorRV!!.itemAnimator = DefaultItemAnimator()
                        tutorRV!!.adapter = mTutorListAdapter
                        mTutorListAdapter!!.notifyDataSetChanged()
                        tutorRV!!.visibility = View.VISIBLE
                        noDataTV!!.visibility = View.GONE
                    } else
                    //For No Data Situation
                    {
                        noDataTV!!.visibility = View.VISIBLE
                        tutorRV!!.visibility = View.GONE
                    }

                }

                override fun onError(err: ServerError) {
                    progressDialog.dismiss()
                    if (activity != null) {
                        Toast.makeText(activity, err.message, Toast.LENGTH_SHORT)
                                .show()

                    }
                }
            })
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed(): Boolean {
        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)
        val newFragment = SingleBookingChooseDateFragment()

        val bundle = Bundle()
        bundle.putString("lesson_id", lessonID)
        bundle.putString("lesson_type", lessonType)
        newFragment.arguments = bundle
        if (fragment == null) {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.framelayout, newFragment).commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, newFragment).commit()
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.single_book))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onPause() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            isPlaying = true
        }

        super.onPause()
    }

    @OnClick(R.id.confirm_tv)
    internal fun confirmBooking() {
        if (Integer.parseInt(tutorID!!) == 0) {
            Toast.makeText(activity, activity.getString(R.string.select_tutor), Toast.LENGTH_LONG)
                    .show()
        } else {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()

            val serverRequestManager = ServerRequestManager(activity)
            serverRequestManager.bookSingleClass(accountID, lessonID, tutorID, startDate, endDate,
                    CommonConstant.lessonBookingType.toString(), lessonType, object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    progressDialog.dismiss()
                    if (result!!.code == ServerResponse.Status.SUCCESS) {
                        showCustomDialog()
                    } else if (result.code == ServerResponse.Status.INSUFFICIENT_CREDIT) {
                        Toast.makeText(activity, result.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(err: ServerError) {
                    progressDialog.dismiss()
                    Log.i("Booking Failed", err.message)
                }
            })
        }
    }

    private fun showCustomDialog() {
        val successDialog = Dialog(activity)
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        successDialog.setContentView(R.layout.single_booking_success_layout)
        successDialog.setCancelable(false)

        val viewSchedule = successDialog.findViewById<TextView>(R.id.tv_my_schedule)
        val bookLesson = successDialog.findViewById<TextView>(R.id.tv_book_lesson)
        val backDashboard = successDialog.findViewById<TextView>(R.id.tv_back_to_main_menu)
        viewSchedule.setOnClickListener { view ->
            successDialog.dismiss()
            activity.finish()
            val intent = Intent(activity, FragmentHolderActivity::class.java)
            intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ScheduleFragment::class.java!!.getSimpleName())
            startActivity(intent)
        }

        bookLesson.setOnClickListener { view ->
            successDialog.dismiss()
            fragmentManager.beginTransaction()
                    .remove(this@SingleBookingChooseTutorFragment)
                    .commit()
            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        backDashboard.setOnClickListener { view ->
            successDialog.dismiss()
            activity.finish()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        successDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    inner class AvailableTutorListAdapter internal constructor() : RecyclerView.Adapter<AvailableTutorListAdapter.ViewHolder>() {

        private var mCurrentPlayingVoice: String? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tutor = avaiTutorList[position]
            if (tutor.tutorId == tutorID)
                holder.checkImg.visibility = View.VISIBLE
            holder.mRatingBar.rating = java.lang.Float.parseFloat(tutor.rate!!)
            holder.tvTutorName.text = tutor.name
            holder.tvTutorExp.text = activity.getString(R.string.years, tutor.teachingExp)
            holder.tvCreditWeight.text = tutor.creditWeight
            holder.tvTutorLocation.text = tutor.country
            if (tutor.avatar != null) {
                holder.sdvTutorPhoto.setImageURI(Uri.parse(tutor.avatar))
            }

            holder.itemView.setOnClickListener { v ->
                if (activity != null)
                    CommonUtil.hideKeyBoard(activity, v)

                val mainFragment = TutorInfoFragment()
                val bundle = Bundle()
                bundle.putString(TutorInfoFragment.EXTRA_TUTOR_ID, tutor.tutorId)
                bundle.putString(TutorInfoFragment.EXTRA_SOURCE, SingleBookingChooseTutorFragment::class.java!!.getSimpleName())
                bundle.putString(TutorInfoFragment.EXTRA_LESSONID, lessonID)
                bundle.putString(TutorInfoFragment.EXTRA_START_DATE, startDate)
                bundle.putString(TutorInfoFragment.EXTRA_END_DATE, endDate)
                bundle.putString("lesson_type", lessonType)
                mainFragment.arguments = bundle

                val fm = activity.fragmentManager
                val fragment = fm.findFragmentById(R.id.framelayout)
                if (fragment == null) {
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.framelayout, mainFragment).commit()
                } else {
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.framelayout, mainFragment).commit()
                }
            }

            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying && tutor.tutorId == mCurrentPlayingVoice) {
                holder.imvPlayVoice.setImageResource(R.mipmap.pause_64)
            } else {
                holder.imvPlayVoice.setImageResource(R.mipmap.play_64)
            }
            holder.imvPlayVoice.setOnClickListener { view ->
                if (!TextUtils.isEmpty(tutor.introVoice)) {
                    if (mMediaPlayer != null && tutor.tutorId != mCurrentPlayingVoice) {
                        if (mMediaPlayer!!.isPlaying)
                            mMediaPlayer!!.stop()
                        mMediaPlayer!!.reset()
                        mMediaPlayer!!.release()
                        mMediaPlayer = null
                        mCurrentPlayingVoice = null
                        mTimer!!.cancel()
                        mTimer = null
                        holder.playPB.progress = 0
                    }

                    val fileName = tutor.introVoice!!.substring(tutor.introVoice!!.lastIndexOf('/') + 1)
                    val file = File(CommonConstant.MEDIA_PATH, fileName)

                    if (file.exists()) {
                        if (mMediaPlayer == null) {
                            mMediaPlayer = MediaPlayer()
                            mMediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                                mMediaPlayer!!.stop()
                                mMediaPlayer!!.reset()
                                mMediaPlayer = null
                                mCurrentPlayingVoice = null
                                holder.imvPlayVoice.setImageResource(R.mipmap.play_64)
                                holder.playPB.progress = 0
                                mTimer!!.cancel()
                                mTimer = null
                                holder.playPB.progress = 0
                                notifyDataSetChanged()
                            }
                            try {
                                mMediaPlayer!!.setDataSource(file.absolutePath)
                                mMediaPlayer!!.prepare()
                                mMediaPlayer!!.start()
                                mCurrentPlayingVoice = tutor.tutorId
                                mTimer = Timer()
                                mTimer!!.schedule(object : TimerTask() {
                                    override fun run() {
                                        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                                            holder.playPB.max = mMediaPlayer!!.duration / 1000
                                            holder.playPB.progress = mMediaPlayer!!.currentPosition / 1000
                                        }
                                    }
                                }, 1000, 1000)

                                if (mMediaPlayer != null && isPlaying) {
                                    mMediaPlayer!!.start()
                                    isPlaying = false
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } else {
                            if (mMediaPlayer!!.isPlaying) {
                                mMediaPlayer!!.pause()
                                holder.imvPlayVoice.setImageResource(R.mipmap.play_64)
                            } else {
                                mMediaPlayer!!.start()
                                mCurrentPlayingVoice = tutor.tutorId
                                mTimer = Timer()
                                mTimer!!.schedule(object : TimerTask() {
                                    override fun run() {
                                        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                                            holder.playPB.max = mMediaPlayer!!.duration / 1000
                                            holder.playPB.progress = mMediaPlayer!!.currentPosition / 1000
                                        }
                                    }
                                }, 1000, 1000)

                                if (mMediaPlayer != null && isPlaying) {
                                    mMediaPlayer!!.start()
                                    isPlaying = false
                                }
                            }
                        }

                        notifyDataSetChanged()
                    } else {
                        val progressDialog = ProgressDialog(activity)
                        progressDialog.setMessage(activity.getString(R.string.loading))
                        progressDialog.setCancelable(false)
                        progressDialog.show()

                        FileDownloader.downloadImage(tutor.introVoice!!, object : FileDownloader.OnDownloadFinishedListener {
                            override fun onSuccess() {
                                progressDialog.dismiss()

                                if (activity != null) {
                                    mMediaPlayer = MediaPlayer()
                                    mMediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                                        mMediaPlayer!!.stop()
                                        mMediaPlayer!!.reset()
                                        mMediaPlayer = null
                                        mCurrentPlayingVoice = null
                                        holder.imvPlayVoice.setImageResource(R.mipmap.play_64)
                                        holder.playPB.progress = 0
                                        mTimer!!.cancel()
                                        mTimer = null
                                        holder.playPB.progress = 0
                                        notifyDataSetChanged()
                                    }

                                    try {
                                        mMediaPlayer!!.setDataSource(file.absolutePath)
                                        mMediaPlayer!!.prepare()
                                        mMediaPlayer!!.start()
                                        mCurrentPlayingVoice = tutor.tutorId
                                        mTimer = Timer()
                                        mTimer!!.schedule(object : TimerTask() {
                                            override fun run() {
                                                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                                                    holder.playPB.max = mMediaPlayer!!.duration / 1000
                                                    holder.playPB.progress = mMediaPlayer!!.currentPosition / 1000
                                                }
                                            }
                                        }, 1000, 1000)

                                        if (mMediaPlayer != null && isPlaying) {
                                            mMediaPlayer!!.start()
                                            isPlaying = false
                                        }
                                        notifyDataSetChanged()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }

                                }
                            }

                            override fun onError() {
                                progressDialog.dismiss()

                                if (activity != null) {
                                    Toast.makeText(activity, activity.getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return avaiTutorList.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvTutorName: TextView
            var tvTutorExp: TextView
            var tvCreditWeight: TextView
            var tvTutorLocation: TextView
            var sdvTutorPhoto: SimpleDraweeView
            var mRatingBar: RatingBar
            var vwSpeaker: View
            var imvPlayVoice: ImageView
            var checkImg: ImageView
            var playPB: ProgressBar

            init {
                mRatingBar = itemView.findViewById(R.id.rb_tutor_rate)
                tvTutorName = itemView.findViewById(R.id.tv_course_title)
                tvTutorExp = itemView.findViewById(R.id.tv_tutor_exp)
                tvCreditWeight = itemView.findViewById(R.id.tv_credit_weight)
                tvTutorLocation = itemView.findViewById(R.id.tv_tutor_location)
                vwSpeaker = itemView.findViewById(R.id.vw_speaker)
                sdvTutorPhoto = itemView.findViewById(R.id.sdv_tutor_photo)
                imvPlayVoice = itemView.findViewById(R.id.imv_intro_voice)
                checkImg = itemView.findViewById(R.id.check_img)
                playPB = itemView.findViewById(R.id.pb_intro_voice)
            }
        }
    }
}
