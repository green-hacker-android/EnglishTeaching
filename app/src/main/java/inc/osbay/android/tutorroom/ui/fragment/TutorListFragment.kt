package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.view.SimpleDraweeView

import org.json.JSONArray
import org.json.JSONException

import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.FileDownloader
import inc.osbay.android.tutorroom.utils.CommonUtil

class TutorListFragment : BackHandledFragment() {

    @BindView(R.id.rv_tutor_list)
    internal var tutorListRV: RecyclerView? = null
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.no_data)
    internal var noDataTV: TextView? = null
    internal var sharedPreferences: SharedPreferences
    internal var isPlaying: Boolean = false
    private var mMediaPlayer: MediaPlayer? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var mTutorDbAdapter: TutorAdapter? = null
    private var mTutorListAdapter: TutorListAdapter? = null
    private var mTutorList: MutableList<Tutor>? = null
    private val mSearchBarRelativeLayout: RelativeLayout? = null
    private var mSearchEditText: EditText? = null
    private val mCurrentPlayingVoice: String? = null
    private val mDrawerLayout: DrawerLayout? = null
    private var mTimer: Timer? = null

    override fun onBackPressed(): Boolean {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
        mTutorDbAdapter = TutorAdapter(activity)
        mTutorList = mTutorDbAdapter!!.allTutor
        mMediaPlayer = MediaPlayer()
        /*mTutorListAdapter = new TutorListAdapter(mMediaPlayer, mTutorList, getActivity(),
                TutorListFragment.class.getSimpleName());*/
        mTutorListAdapter = TutorListAdapter()
        mTimer = Timer()
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        /*setTitle(getString(R.string.tu_lst_title));
        setDisplayHomeAsUpEnable(true);*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_list, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)

        val searchCancelImageView = view.findViewById<ImageView>(R.id.imv_search_cancel)
        searchCancelImageView.setOnClickListener { view1 ->
            mSearchBarRelativeLayout!!.visibility = View.INVISIBLE
            mSearchEditText!!.isEnabled = false

            if (activity != null)
                CommonUtil.hideKeyBoard(activity, view1)
        }

        mSearchEditText = view.findViewById(R.id.edt_search_text)
        mSearchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val tutorName = mSearchEditText!!.text.toString()
                if (!TextUtils.isEmpty(tutorName)) {
                    mTutorList = mTutorDbAdapter!!.searchTutorByName(tutorName)
                } else {
                    mTutorList = mTutorDbAdapter!!.allTutor
                }
                mTutorListAdapter!!.notifyDataSetChanged()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        val mLayoutManager = LinearLayoutManager(activity)
        tutorListRV!!.layoutManager = mLayoutManager
        tutorListRV!!.itemAnimator = DefaultItemAnimator()
        tutorListRV!!.adapter = mTutorListAdapter
        noDataTV!!.visibility = View.GONE
        return view
    }

    override fun onPause() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            isPlaying = true
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        setDisplayHomeAsUpEnable(true)
        setTitle(getString(R.string.tu_lst_title))

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))

        if (mTutorList!!.size == 0) {
            progressDialog.show()
        }

        mServerRequestManager!!.getTutorList(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                progressDialog.dismiss()
                if (result!!.code == 1)
                //For Success Situation
                {
                    tutorListRV!!.visibility = View.VISIBLE
                    noDataTV!!.visibility = View.GONE
                    if (activity != null) {
                        mTutorList!!.clear()

                        try {
                            val jsonArray = JSONArray(result.dataSt)
                            for (i in 0 until jsonArray.length()) {
                                val tutor = Tutor(jsonArray.getJSONObject(i))
                                mTutorList!!.add(tutor)
                            }
                        } catch (je: JSONException) {
                            Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je)
                        }

                    }
                    mTutorListAdapter!!.notifyDataSetChanged()
                } else
                //For No Data Situation
                {
                    noDataTV!!.visibility = View.VISIBLE
                    noDataTV!!.text = getString(R.string.no_tutor)
                    tutorListRV!!.visibility = View.GONE
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()

                if (activity != null) {
                    Toast.makeText(activity, getString(R.string.tu_lst_refresh_failed), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tutorlist_menu, menu);
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }/*case R.id.opt_search:
                mSearchBarRelativeLayout.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mSearchEditText.setEnabled(true);
                mSearchEditText.requestFocus();
                imm.toggleSoftInputFromWindow(mSearchEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                //hideActionBar();
                return true;*/
        return true
    }

    inner class TutorListAdapter internal constructor() : RecyclerView.Adapter<TutorListAdapter.ViewHolder>() {

        private var isPlaying: Boolean = false
        private var mCurrentPlayingVoice: String? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorListAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
            return TutorListAdapter.ViewHolder(v)
        }

        override fun onBindViewHolder(holder: TutorListAdapter.ViewHolder, position: Int) {
            val tutor = mTutorList!![position]
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
                bundle.putString(TutorInfoFragment.EXTRA_SOURCE, TutorListFragment::class.java!!.getSimpleName())
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
            return mTutorList!!.size
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