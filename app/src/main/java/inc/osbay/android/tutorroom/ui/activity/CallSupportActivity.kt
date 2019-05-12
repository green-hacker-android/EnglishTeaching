package inc.osbay.android.tutorroom.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.internal.Utils.listOf

import com.twilio.video.ConnectOptions
import com.twilio.video.LocalAudioTrack
import com.twilio.video.RemoteAudioTrack
import com.twilio.video.RemoteAudioTrackPublication
import com.twilio.video.RemoteDataTrack
import com.twilio.video.RemoteDataTrackPublication
import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrack
import com.twilio.video.RemoteVideoTrackPublication
import com.twilio.video.Room
import com.twilio.video.TwilioException
import com.twilio.video.Video

import org.apache.log4j.Logger

import java.util.Collections

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.ui.fragment.OnlineSupportFragment
import inc.osbay.android.tutorroom.utils.CommonUtil

class CallSupportActivity : AppCompatActivity(), View.OnClickListener {
    private var mAccessToken: String? = null
    private val mLog: Logger? = null
    private var isMuted: Boolean = false
    private var mLocalAudioTrack: LocalAudioTrack? = null
    private var mCallingTextView: TextView? = null
    private var mMuteVoiceImageView: ImageView? = null
    private var mSpeakerOnImageView: ImageView? = null
    private var mEarPhoneRegister: EarPhoneRegister? = null
    private var mAudioManager: AudioManager? = null
    /*
     * A Room represents communication between a local participant and one or more participants.
     */
    private var mRoom: Room? = null

    private var previousAudioMode: Int = 0
    private var previousMicrophoneMute: Boolean = false
    private var mConsultantId: String? = null
    private var mParticipantIdentity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_support)

        val requestManager = ServerRequestManager(this)

        val intent = intent
        if (intent != null) {
            mConsultantId = intent.getStringExtra(EXTRA_CONSULTANT_ID)
            mAccessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN)

            /*requestManager.savePhoneHistory(mConsultantId, new ServerRequestManager.OnRequestFinishedListener() {
                @Override
                public void onSuccess(ServerResponse result) {
                }

                @Override
                public void onError(ServerError err) {
                }
            });*/
        }

        val llEndCall = findViewById<LinearLayout>(R.id.ll_end_call)
        llEndCall.setOnClickListener(this)

        val mHideCallImageView = findViewById<ImageView>(R.id.imv_hide_call)
        mHideCallImageView.setOnClickListener(this)

        mSpeakerOnImageView = findViewById(R.id.imv_speaker_on)
        mSpeakerOnImageView!!.setOnClickListener(this)

        mMuteVoiceImageView = findViewById(R.id.imv_mute_voice)
        mMuteVoiceImageView!!.setOnClickListener(this)

        mCallingTextView = findViewById(R.id.tv_calling)

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        /*
         * Needed for setting/abandoning audio focus during call
         */
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkPermissionForSDCard()) {
                Log4jHelper log4jHelper = new Log4jHelper();
                if (mLog != null)
                    mLog = log4jHelper.getLogger("CallSupportActivity");
            }
        } else {
            Log4jHelper log4jHelper = new Log4jHelper();
            mLog = log4jHelper.getLogger("CallSupportActivity");
        }*/

        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone()
        } else {
            createAudioTracks()
            if (mAccessToken != null) {
                connectToRoom(CommonUtil.generateCallSupportRoomId(mConsultantId!!))
            }
        }

        mEarPhoneRegister = EarPhoneRegister()
    }

    private fun createAudioTracks() {
        // Share your microphone
        mLocalAudioTrack = LocalAudioTrack.create(this, true)
    }

    private fun connectToRoom(roomName: String) {
        configureAudio(true)

        val connectOptionsBuilder = ConnectOptions.Builder(mAccessToken)
                .roomName(roomName)

        /*
         * Add local audio track to connect options to share with participants.
         */
        if (mLocalAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(listOf<LocalAudioTrack>(mLocalAudioTrack))
        }

        mRoom = Video.connect(this, connectOptionsBuilder.build(), roomListener())
    }

    /*
     * Called when participant joins the room
     */
    private fun addParticipant(participant: RemoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        mParticipantIdentity = participant.identity

        /*
         * Start listening for participant events
         */
        participant.setListener(participantListener())
    }

    /*
     * Called when participant leaves the room
     */
    private fun removeParticipant(participant: RemoteParticipant) {
        if (participant.identity != mParticipantIdentity) {
            return
        }

        finish()
    }

    private fun configureAudio(enable: Boolean) {
        if (enable) {
            previousAudioMode = mAudioManager!!.mode

            mAudioManager!!.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)

            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            mAudioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION

            /*
             * Always disable microphone mute during a WebRTC call.
             */
            previousMicrophoneMute = mAudioManager!!.isMicrophoneMute
            mAudioManager!!.isMicrophoneMute = false
        } else {
            mAudioManager!!.mode = previousAudioMode
            mAudioManager!!.abandonAudioFocus(null)
            mAudioManager!!.isMicrophoneMute = previousMicrophoneMute
        }
    }

    private fun participantListener(): RemoteParticipant.Listener {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {

            }

            override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {

            }

            override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, remoteAudioTrack: RemoteAudioTrack) {
                /*if (mLog != null)
                    mLog.error("onAudioTrackAdded");*/
            }

            override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, twilioException: TwilioException) {

            }

            override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, remoteAudioTrack: RemoteAudioTrack) {
                /*if (mLog != null)
                    mLog.error("onAudioTrackRemoved");*/
            }

            override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }

            override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }

            override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, remoteVideoTrack: RemoteVideoTrack) {
                /*if (mLog != null)
                    mLog.error("onVideoTrackAdded");*/
            }

            override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, twilioException: TwilioException) {

            }

            override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, remoteVideoTrack: RemoteVideoTrack) {
                /*if (mLog != null)
                    mLog.error("onVideoTrackRemoved");*/
            }

            override fun onDataTrackPublished(remoteParticipant: RemoteParticipant, remoteDataTrackPublication: RemoteDataTrackPublication) {

            }

            override fun onDataTrackUnpublished(remoteParticipant: RemoteParticipant, remoteDataTrackPublication: RemoteDataTrackPublication) {

            }

            override fun onDataTrackSubscribed(remoteParticipant: RemoteParticipant, remoteDataTrackPublication: RemoteDataTrackPublication, remoteDataTrack: RemoteDataTrack) {

            }

            override fun onDataTrackSubscriptionFailed(remoteParticipant: RemoteParticipant, remoteDataTrackPublication: RemoteDataTrackPublication, twilioException: TwilioException) {

            }

            override fun onDataTrackUnsubscribed(remoteParticipant: RemoteParticipant, remoteDataTrackPublication: RemoteDataTrackPublication, remoteDataTrack: RemoteDataTrack) {

            }

            override fun onAudioTrackEnabled(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {
                /*if (mLog != null)
                    mLog.error("onAudioTrackEnabled");*/
            }

            override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {
                /*if (mLog != null)
                    mLog.error("onAudioTrackDisabled");*/
            }

            override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {
                /*if (mLog != null)
                    mLog.error("onVideoTrackEnabled");*/
            }

            override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {
                /*if (mLog != null)
                    mLog.error("onVideoTrackDisabled");*/
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*if (mLog != null)
            mLog.error("On Resume");*/

        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(mEarPhoneRegister, filter)
    }

    public override fun onPause() {
        super.onPause()
        /*if (mLog != null)
            mLog.error("On Pause");*/

        unregisterReceiver(mEarPhoneRegister)
    }

    override fun onDestroy() {
        if (CallSupportActivity.sIsTalking) {
            hangUp()

            CallSupportActivity.sIsTalking = false
        }

        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MIC_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mAccessToken != null) {
                        createAudioTracks()
                        connectToRoom(CommonUtil.generateCallSupportRoomId(mConsultantId!!))
                    }
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(
                            this)

                    alertDialogBuilder.setTitle(getString(R.string.permission))
                    alertDialogBuilder.setMessage(getString(R.string.check_perm))
                    alertDialogBuilder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.imv_speaker_on -> if (mAudioManager!!.isSpeakerphoneOn) {
                mAudioManager!!.isSpeakerphoneOn = false
                mSpeakerOnImageView!!.setImageResource(R.mipmap.ic_speaker_off)
            } else {
                mAudioManager!!.isSpeakerphoneOn = true
                mSpeakerOnImageView!!.setImageResource(R.mipmap.ic_speaker_on)
            }
            R.id.imv_mute_voice -> if (isMuted) {
                isMuted = false
                mLocalAudioTrack!!.enable(true)
                mMuteVoiceImageView!!.setImageResource(R.mipmap.ic_mute_off)
            } else {
                isMuted = true
                mLocalAudioTrack!!.enable(false)
                mMuteVoiceImageView!!.setImageResource(R.mipmap.ic_mute_on)
            }
            R.id.imv_hide_call -> moveTaskToBack(true)
            R.id.ll_end_call -> {
                if (mRoom != null) {
                    OnlineSupportFragment.showError(107)
                    mRoom!!.disconnect()
                }

                this@CallSupportActivity.finish()
            }
            else -> {
            }
        }
    }

    /*
     * Room events listener
     */
    private fun roomListener(): Room.Listener {
        return object : Room.Listener {
            override fun onConnected(room: Room) {
                if (room.remoteParticipants.size == 1) {
                    for (participant in room.remoteParticipants) {
                        if (participant.identity == "A_" + mConsultantId!!) {
                            sIsTalking = true

                            addParticipant(participant)
                            break
                        } else {
                            sIsTalking = false

                            OnlineSupportFragment.showError(106)
                            hangUp()
                        }
                    }
                } else {
                    sIsTalking = false

                    OnlineSupportFragment.showError(106)
                    hangUp()
                }
                mCallingTextView!!.visibility = View.INVISIBLE
            }

            override fun onConnectFailure(room: Room, e: TwilioException) {
                configureAudio(false)

                sIsTalking = false

                if (this@CallSupportActivity != null) {
                    AlertDialog.Builder(this@CallSupportActivity)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.os_dialog_connect_failure_msg))
                            .setPositiveButton(getString(R.string.ok), null)
                            .setOnDismissListener { dialogInterface -> onBackPressed() }
                            .create()
                            .show()
                }
            }

            override fun onDisconnected(room: Room, e: TwilioException) {
                if (this@CallSupportActivity != null) {
                    this@CallSupportActivity.mRoom = null

                    if (sIsTalking) {
                        sIsTalking = false
                    }
                    configureAudio(false)

                    /*Intent mainActivity = new Intent(CallSupportActivity.this, MainActivity.class);
                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putString("class_type", "normal");
                    mainActivity.putExtras(bundle);
                    startActivity(mainActivity);*/

                    this@CallSupportActivity.finish()
                }
            }

            override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
                /*if (mLog != null)
                    mLog.error("RemoteParticipant disconnected from room = " + room.getName() + "/" + room.getSid() +
                            ", participant - " + participant.getIdentity() + "/" + participant.getSid());*/

                addParticipant(participant)
            }

            override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
                /*if (mLog != null)
                    mLog.error("RemoteParticipant disconnected from room = " + room.getName() + "/" + room.getSid() +
                            ", participant - " + participant.getIdentity() + "/" + participant.getSid());*/

                if (participant.identity == "A_" + mConsultantId!!) {
                    OnlineSupportFragment.showError(107)
                    room.disconnect()
                }

                //                removeParticipant(participant);
            }

            override fun onRecordingStarted(room: Room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                //                Log.d(TAG, "onRecordingStarted");
            }

            override fun onRecordingStopped(room: Room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                //                Log.d(TAG, "onRecordingStopped");
            }
        }
    }

    private fun hangUp() {
        if (mRoom != null)
            mRoom!!.disconnect()

        this@CallSupportActivity.finish()
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.cr_camera_permission,
                    Toast.LENGTH_LONG).show()
            finish()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MIC_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkPermissionForSDCard(): Boolean {
        val resultReadSD = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val resultWriteSD = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return resultReadSD == PackageManager.PERMISSION_GRANTED && resultWriteSD == PackageManager.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        if (sIsTalking) {
            moveTaskToBack(true)
        }
    }

    private inner class EarPhoneRegister : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                    val state = intent.getIntExtra("state", -1)
                    when (state) {
                        0 -> if (mAudioManager!!.isSpeakerphoneOn) {
                            mAudioManager!!.isSpeakerphoneOn = true
                            mSpeakerOnImageView!!.setImageResource(R.mipmap.ic_speaker_on)
                        } else {
                            mAudioManager!!.isSpeakerphoneOn = false
                            mSpeakerOnImageView!!.setImageResource(R.mipmap.ic_speaker_off)
                        }
                        1 -> {
                            if (mAudioManager!!.isSpeakerphoneOn) {
                                mSpeakerOnImageView!!.setImageResource(R.mipmap.ic_speaker_off)
                            }
                            mAudioManager!!.isSpeakerphoneOn = false
                        }
                        else -> {
                        }
                    }/*if (mLog != null)
                                mLog.error("Headset error.");*/
                }
            }
        }
    }

    companion object {

        val EXTRA_CONSULTANT_ID = "CallSupportActivity.EXTRA_CONSULTANT_ID"

        val EXTRA_ACCESS_TOKEN = "CallSupportActivity.EXTRA_ACCESS_TOKEN"

        private val MIC_PERMISSION_REQUEST_CODE = 1

        var sIsTalking: Boolean = false
    }
}
