package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.view.SimpleDraweeView

import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.FileDownloader

class TutorInfoFragment : BackHandledFragment() {
    @BindView(R.id.pb_intro_voice)
    internal var mMusicProgressBar: ProgressBar? = null
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.tv_course_title)
    internal var tvTutorName: TextView? = null
    @BindView(R.id.rb_tutor_rate)
    internal var rbRate: RatingBar? = null
    @BindView(R.id.self_intro_tv)
    internal var tvIntroText: TextView? = null
    @BindView(R.id.rate_tv)
    internal var tvCreditWeight: TextView? = null
    @BindView(R.id.speciality_label)
    internal var speciality: TextView? = null
    @BindView(R.id.teaching_exp_label)
    internal var tvExp: TextView? = null
    @BindView(R.id.location_label)
    internal var location: TextView? = null
    @BindView(R.id.sdv_tutor_photo)
    internal var sdvTutorPhoto: SimpleDraweeView? = null
    @BindView(R.id.imv_intro_voice)
    internal var imvIntroVoice: ImageView? = null
    @BindView(R.id.book_btn_tv)
    internal var bookTV: TextView? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mTimer: Timer? = null
    private var isPlaying: Boolean = false
    private var mTutorTime: String? = null
    private var mBookingType: Int = 0
    private var mAccount: Account? = null
    private var mTutor: Tutor? = null
    private var tutorID: String? = null
    private var source: String? = null
    private var lessonID: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var lessonType: String? = null


    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = TutorAdapter(activity)
        val bundle = arguments
        if (bundle != null) {
            source = bundle.getString(EXTRA_SOURCE)
            if (source == SingleBookingChooseTutorFragment::class.java!!.getSimpleName()) {
                lessonType = arguments.getString("lesson_type")
                lessonID = arguments.getString(EXTRA_LESSONID)
                startDate = arguments.getString(EXTRA_START_DATE)
                endDate = arguments.getString(EXTRA_END_DATE)
            }
            tutorID = bundle.getString(EXTRA_TUTOR_ID)
            mTutor = adapter.getTutorById(tutorID!!)
            mTutorTime = bundle.getString(EXTRA_TUTOR_TIME)
            mBookingType = bundle.getInt(EXTRA_BOOKING_TYPE)
        }

        val mPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val accountId = mPreferences.getInt("account_id", 0)
        if (accountId != 0) {
            val DBAdapter = DBAdapter(activity)
            mAccount = DBAdapter.getAccountById(accountId.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.fragment_tutor_info, container, false)
        ButterKnife.bind(this, rootView)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)

        tvTutorName!!.text = mTutor!!.name
        rbRate!!.rating = java.lang.Float.parseFloat(mTutor!!.rate!!)
        tvIntroText!!.text = mTutor!!.introduction
        tvCreditWeight!!.text = mTutor!!.creditWeight
        speciality!!.text = getString(R.string.speciality, mTutor!!.speciality)
        tvExp!!.text = getString(R.string.teach_experience, mTutor!!.teachingExp)
        location!!.text = getString(R.string.locationn, mTutor!!.location)
        if (mTutor!!.avatar != null) {
            sdvTutorPhoto!!.setImageURI(Uri.parse(mTutor!!.avatar))
        }
        if (source == SingleBookingChooseTutorFragment::class.java!!.getSimpleName())
            bookTV!!.visibility = View.VISIBLE
        else
            bookTV!!.visibility = View.GONE
        return rootView
    }

    override fun onStart() {
        super.onStart()
        if (source == SingleBookingChooseTutorFragment::class.java!!.getSimpleName())
            setTitle(getString(R.string.single_book))
        else
            setTitle(getString(R.string.tutor))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                    mMusicProgressBar!!.max = mMediaPlayer!!.duration / 1000
                    mMusicProgressBar!!.progress = mMediaPlayer!!.currentPosition / 1000
                }
            }
        }, 1000, 1000)

        if (mMediaPlayer != null && isPlaying) {
            mMediaPlayer!!.start()
            isPlaying = false
        }
    }

    override fun onPause() {
        super.onPause()
        mTimer!!.cancel()

        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            isPlaying = true
        }
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        super.onDestroy()
    }

    @OnClick(R.id.book_btn_tv)
    internal fun bookTutor() {
        if (source == SingleBookingChooseTutorFragment::class.java!!.getSimpleName()) {
            fragmentManager.popBackStack()
            val mainFragment = SingleBookingChooseTutorFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_TUTOR_ID, tutorID)
            bundle.putString("source", TutorInfoFragment::class.java!!.getSimpleName())
            bundle.putString("lesson_id", lessonID)
            bundle.putString("start_date", startDate)
            bundle.putString("end_date", endDate)
            bundle.putString("lesson_type", lessonType)
            mainFragment.arguments = bundle

            val fm = fragmentManager
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
    }


    @OnClick(R.id.imv_intro_voice)
    internal fun playIntroVoice() {
        if (!TextUtils.isEmpty(mTutor!!.introVoice)) {
            val fileName = mTutor!!.introVoice!!.substring(mTutor!!.introVoice!!.lastIndexOf('/') + 1, mTutor!!.introVoice!!.length)
            val file = File(CommonConstant.MEDIA_PATH, fileName)

            if (file.exists()) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer()
                    mMediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                        mMediaPlayer!!.stop()
                        mMediaPlayer!!.reset()
                        mMediaPlayer = null
                        imvIntroVoice!!.setImageResource(R.mipmap.play_64)
                        mMusicProgressBar!!.progress = 0
                    }
                    try {
                        mMediaPlayer!!.setDataSource(file.absolutePath)
                        mMediaPlayer!!.prepare()
                        mMediaPlayer!!.start()
                        imvIntroVoice!!.setImageResource(R.mipmap.pause_64)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    if (mMediaPlayer!!.isPlaying) {
                        mMediaPlayer!!.pause()
                        imvIntroVoice!!.setImageResource(R.mipmap.play_64)
                    } else {
                        mMediaPlayer!!.start()
                        imvIntroVoice!!.setImageResource(R.mipmap.pause_64)
                    }
                }
            } else {

                val progressDialog = ProgressDialog(activity)
                progressDialog.setMessage(getString(R.string.loading))
                progressDialog.setCancelable(false)
                progressDialog.show()

                FileDownloader.downloadImage(mTutor!!.introVoice!!, object : FileDownloader.OnDownloadFinishedListener {
                    override fun onSuccess() {
                        progressDialog.dismiss()

                        mMediaPlayer = MediaPlayer()
                        mMediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                            mMediaPlayer!!.stop()
                            mMediaPlayer!!.reset()
                            mMediaPlayer = null

                            imvIntroVoice!!.setImageResource(R.mipmap.play_64)
                            mMusicProgressBar!!.progress = 0
                        }
                        try {
                            mMediaPlayer!!.setDataSource(file.absolutePath)
                            mMediaPlayer!!.prepare()
                            mMediaPlayer!!.start()

                            imvIntroVoice!!.setImageResource(R.mipmap.pause_64)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError() {
                        progressDialog.dismiss()
                        Toast.makeText(activity, getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    companion object {
        val EXTRA_TUTOR_ID = "TutorInfoFragment.EXTRA_TUTOR_ID"
        val EXTRA_TUTOR_TIME = "TutorInfoFragment.EXTRA_TUTOR_TIME"
        val EXTRA_BOOKING_TYPE = "TutorInfoFragment.EXTRA_BOOKING_TYPE"
        val EXTRA_SOURCE = "TutorInfoFragment.EXTRA_SOURCE"
        val EXTRA_LESSONID = "TutorInfoFragment.EXTRA_LESSONID"
        val EXTRA_START_DATE = "TutorInfoFragment.EXTRA_START_DATE"
        val EXTRA_END_DATE = "TutorInfoFragment.EXTRA_END_DATE"
    }
}
