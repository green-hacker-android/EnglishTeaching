package inc.osbay.android.tutorroom.adapter

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
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

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.FileDownloader
import inc.osbay.android.tutorroom.ui.fragment.SingleBookingChooseTutorFragment
import inc.osbay.android.tutorroom.ui.fragment.TutorInfoFragment
import inc.osbay.android.tutorroom.utils.CommonUtil

class TutorListAdapter(private var mMediaPlayer: MediaPlayer?, private val mTutorList: List<Tutor>, private val context: Context?, private val source: String) : RecyclerView.Adapter<TutorListAdapter.ViewHolder>() {

    private var isPlaying: Boolean = false
    private var mCurrentPlayingVoice: String? = null

    init {
        this.mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutor = mTutorList[position]
        holder.mRatingBar.rating = java.lang.Float.parseFloat(tutor.rate!!)
        holder.tvTutorName.text = tutor.name
        holder.tvTutorExp.text = context!!.getString(R.string.years, tutor.teachingExp)
        holder.tvCreditWeight.text = tutor.creditWeight
        holder.tvTutorLocation.text = tutor.country

        if (tutor.avatar != null) {
            holder.sdvTutorPhoto.setImageURI(Uri.parse(tutor.avatar))
        }

        holder.itemView.setOnClickListener { v ->
            if (context != null)
                CommonUtil.hideKeyBoard(context, v)

            val mainFragment = TutorInfoFragment()
            val bundle = Bundle()
            bundle.putString(TutorInfoFragment.EXTRA_TUTOR_ID, tutor.tutorId)
            bundle.putString(TutorInfoFragment.EXTRA_SOURCE, source)
            mainFragment.arguments = bundle

            val fm = (context as Activity).fragmentManager
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
                }

                val fileName = tutor.introVoice!!.substring(tutor.introVoice!!.lastIndexOf('/') + 1, tutor.introVoice!!.length)
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
                            notifyDataSetChanged()
                        }
                        try {
                            mMediaPlayer!!.setDataSource(file.absolutePath)
                            mMediaPlayer!!.prepare()
                            mMediaPlayer!!.start()
                            mCurrentPlayingVoice = tutor.tutorId
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
                        }
                    }

                    notifyDataSetChanged()
                } else {

                    val progressDialog = ProgressDialog(context)
                    progressDialog.setMessage(context.getString(R.string.loading))
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    FileDownloader.downloadImage(tutor.introVoice!!, object : FileDownloader.OnDownloadFinishedListener {
                        override fun onSuccess() {
                            progressDialog.dismiss()

                            if (context != null) {
                                mMediaPlayer = MediaPlayer()
                                mMediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                                    mMediaPlayer!!.stop()
                                    mMediaPlayer!!.reset()
                                    mMediaPlayer = null
                                    mCurrentPlayingVoice = null
                                    holder.imvPlayVoice.setImageResource(R.mipmap.play_64)
                                    holder.playPB.progress = 0

                                    notifyDataSetChanged()
                                }

                                try {
                                    mMediaPlayer!!.setDataSource(file.absolutePath)
                                    mMediaPlayer!!.prepare()
                                    mMediaPlayer!!.start()
                                    mCurrentPlayingVoice = tutor.tutorId
                                    notifyDataSetChanged()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                            }
                        }

                        override fun onError() {
                            progressDialog.dismiss()

                            if (context != null) {
                                Toast.makeText(context, context.getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }

            val mTimer = Timer()
            mTimer.schedule(object : TimerTask() {
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

    override fun getItemCount(): Int {
        return mTutorList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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