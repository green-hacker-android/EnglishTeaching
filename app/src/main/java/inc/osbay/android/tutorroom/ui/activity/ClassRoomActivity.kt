package inc.osbay.android.tutorroom.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.twilio.video.CameraCapturer
import com.twilio.video.ConnectOptions
import com.twilio.video.LocalAudioTrack
import com.twilio.video.LocalParticipant
import com.twilio.video.LocalVideoTrack
import com.twilio.video.RemoteAudioTrack
import com.twilio.video.RemoteAudioTrackPublication
import com.twilio.video.RemoteDataTrack
import com.twilio.video.RemoteDataTrackPublication
import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrack
import com.twilio.video.RemoteVideoTrackPublication
import com.twilio.video.Room
import com.twilio.video.RoomState
import com.twilio.video.TwilioException
import com.twilio.video.Video
import com.twilio.video.VideoTrack
import com.twilio.video.VideoView

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.util.ArrayList
import java.util.Collections

import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.TRApplication
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.Booking
import inc.osbay.android.tutorroom.sdk.model.ChatMessage
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.service.MessengerService
import inc.osbay.android.tutorroom.ui.fragment.ClassroomFAQFragment
import inc.osbay.android.tutorroom.utils.CameraCapturerCompat
import inc.osbay.android.tutorroom.utils.CommonUtil
import inc.osbay.android.tutorroom.utils.SharedPreferenceData
import inc.osbay.android.tutorroom.utils.WSMessageClient


class ClassRoomActivity : AppCompatActivity(), View.OnClickListener {
    internal lateinit var sharedPreferenceData: SharedPreferenceData
    private var mWebView: WebView? = null
    private var mWSMessageClient: WSMessageClient? = null
    private var rvChatMessages: RecyclerView? = null
    //* A Room represents communication between a local participant and one or more participants.
    private var mRoom: Room? = null
    private var mInputEditText: EditText? = null
    //private ImageView mLocalVideoHideImageView;
    private var mClassStatusRelativeLayout: RelativeLayout? = null
    private var mClassStatusTextView: TextView? = null
    private var mAudioManager: AudioManager? = null
    private var mMessenger: Messenger? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var mEarPhoneRegister: EarPhoneRegister? = null
    private var mLocalVideoTrack: LocalVideoTrack? = null
    private var mLocalAudioTrack: LocalAudioTrack? = null
    private var mLocalVideoView: VideoView? = null
    private var mRemoteVideoView: VideoView? = null
    private var mTutorPhotoDraweeView: SimpleDraweeView? = null
    private var mLocalParticipant: LocalParticipant? = null
    private val pauseVideo: Boolean = false
    private var tutorParticipant: String? = null
    private var mPreviousAudioMode: Int = 0
    private var mPreviousMicrophoneMute: Boolean = false
    private var disconnectedFromOnDestroy: Boolean = false
    private var mRlWhiteBoard: RelativeLayout? = null
    private var dY: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()
    private var mVideoView: VideoView? = null
    private var classRoomLayout: RelativeLayout? = null
    private var newY: Float = 0.toFloat()
    private var newX: Float = 0.toFloat()
    private var screenHight: Int = 0
    private var screenWidth: Int = 0
    private var pressStartTime: Long = 0
    private var pressX: Float = 0.toFloat()
    private var pressY: Float = 0.toFloat()
    private var distance: Float = 0.toFloat()
    private var menuRL: RelativeLayout? = null
    private var mCameraCapturerCompat: CameraCapturerCompat? = null
    private var roomLockID: String? = null

    fun getInstance(): WebView.WebViewTransport {
        if (instance == null) {
            instance = mWebView!!.WebViewTransport()
        }
        return instance as WebView.WebViewTransport
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(accountName: String?, boardId: String?) {
        mWebView = WebView(applicationContext)
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.domStorageEnabled = true
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                mWebView!!.loadUrl(url)
                return true
            }
        }

        mWebView!!.loadUrl(CommonConstant.WHITEBOARD_URL + "?boardId=" +
                boardId + "&studentName="
                + accountName)
        getInstance().webView = mWebView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_room)
        ButterKnife.bind(this)

        sharedPreferenceData = SharedPreferenceData(this)
        accountId = sharedPreferenceData.getInt("account_id").toString()
        accountName = sharedPreferenceData.getString("account_name")

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        screenHight = displaymetrics.heightPixels
        screenWidth = displaymetrics.widthPixels

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val tvTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        //tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Bold.ttf"));

        val closeImg = findViewById<ImageView>(R.id.cross_img)
        closeImg.setOnClickListener(this)

        val dbAdapter = DBAdapter(this)
        val tutorAdapter = TutorAdapter(this)

        val intent = intent
        if (intent != null) {
            mBooking = intent.getSerializableExtra(EXTRA_BOOKING) as Booking
            mAccount = dbAdapter.getAccountById(accountId!!)
            mTutor = tutorAdapter.getTutorById(mBooking!!.tutorId!!)
        }

        mClassStatusTextView = findViewById(R.id.tv_class_status)
        mClassStatusRelativeLayout = findViewById(R.id.rl_class_status)
        mInputEditText = findViewById(R.id.edt_input_text)
        mLocalVideoView = findViewById(R.id.vv_local_video)
        mRemoteVideoView = findViewById(R.id.twilio_vv_remote_video)
        mRlWhiteBoard = findViewById(R.id.rl_white_board)
        mVideoView = findViewById(R.id.twilio_remote_video)
        classRoomLayout = findViewById(R.id.classroom_layout)
        menuRL = findViewById(R.id.rl_assistant_pop_up)
        val onlineSupportLL = findViewById<LinearLayout>(R.id.ll_assistant_online_support)
        val faqLL = findViewById<LinearLayout>(R.id.ll_assistant_faq)
        onlineSupportLL?.setOnClickListener(this)
        faqLL?.setOnClickListener(this)
        val assistantImg = findViewById<ImageView>(R.id.assistant_menu)
        assistantImg?.setOnClickListener(this)

        val mSendButtonTextView = findViewById<TextView>(R.id.tv_send_button)
        mSendButtonTextView?.setOnClickListener(this)

        val fabWhiteBoard = findViewById<FloatingActionButton>(R.id.fab_white_board)
        fabWhiteBoard?.setOnClickListener(this)

        rvChatMessages = findViewById(R.id.rv_chat_messages)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        if (rvChatMessages != null) {
            rvChatMessages!!.layoutManager = layoutManager
        }

        mMessageAdapter = MessageAdapter()
        if (rvChatMessages != null) {
            rvChatMessages!!.adapter = mMessageAdapter
        }

        mTutorPhotoDraweeView = findViewById(R.id.sdv_tutor_photo)
        val controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.mipmap.spinner))
                .setAutoPlayAnimations(true)
                .build()
        mTutorPhotoDraweeView!!.controller = controller

        /*mLocalVideoHideImageView = findViewById(R.id.imv_local_video_hide);
        if (mLocalVideoHideImageView != null) {
            mLocalVideoHideImageView.setOnClickListener(this);
        }*/

        mEarPhoneRegister = EarPhoneRegister()

        //* Enable changing the volume using the up/down keys during a conversation
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        // Needed for setting/abandoning audio focus during call
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkPermissionForSDCard()) {
                Log4jHelper log4jHelper = new Log4jHelper();
                if (mLog != null)
                    mLog = log4jHelper.getLogger("ClassroomActivity");
            }
        } else {
            Log4jHelper log4jHelper = new Log4jHelper();
            mLog = log4jHelper.getLogger("ClassroomActivity");
        }*/

        if (!isTalking) {
            //loadWebView(mBooking.getStudentId(), mBooking.getBookingId());
            mWSMessageClient = (application as TRApplication).wsMessageClient
            mServerRequestManager = ServerRequestManager(applicationContext)

            mServerRequestManager!!.getRoomCodeByRoomID(mBooking!!.bookingId!!, object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    var whiteboardID: String? = null
                    try {
                        val dataObject = JSONObject(result!!.dataSt)
                        whiteboardID = dataObject.getString("board_id")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    loadWebView(accountName, whiteboardID)

                    if (!checkPermissionForCameraAndMicrophone()) {
                        requestPermissionForCameraAndMicrophone()
                    } else {
                        initializeTwilioSDK()
                    }
                }

                override fun onError(err: ServerError) {
                    Log.i("Get Whiteboard ID Error", err.message)
                }
            })
        } else {
            createAudioAndVideoTracks()
        }
    }

    override fun onResume() {
        super.onResume()

        /*if (mLog != null)
            mLog.debug("On Resume");*/

        val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(mEarPhoneRegister, intentFilter)
        mMessenger = Messenger(IncomingHandler())
        mWSMessageClient!!.addMessenger(mMessenger!!)

        if (mLocalVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            mCameraCapturerCompat = CameraCapturerCompat(this, CameraCapturer.CameraSource.FRONT_CAMERA)
            mLocalVideoTrack = LocalVideoTrack.create(this, true, mCameraCapturerCompat!!.videoCapturer!!)
            mLocalVideoTrack!!.addRenderer(mLocalVideoView!!)

            //* If connected to a Room then share the local video track.


            if (mLocalParticipant != null) {
                mLocalParticipant!!.publishTrack(mLocalVideoTrack!!)
            }
        }
    }

    public override fun onPause() {
        /*if (mLog != null)
            mLog.debug("On Pause");*/

        unregisterReceiver(mEarPhoneRegister)
        mWSMessageClient!!.removeMessenger(mMessenger!!)
        super.onPause()
    }

    override fun onDestroy() {

        if (!isTalking) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            finishActivity(1010)
            mMessageList.clear()
        }

        //* Always disconnect from the room before leaving the Activity to
        //* ensure any memory allocated to the Room resource is freed.


        if (mRoom != null && mRoom!!.state != RoomState.DISCONNECTED) {
            mRoom!!.disconnect()
            disconnectedFromOnDestroy = true
        }

        //* Release the local audio and video tracks ensuring any memory allocated to audio
        //* or video is freed.


        if (mLocalAudioTrack != null) {
            mLocalAudioTrack!!.release()
            mLocalAudioTrack = null
        }
        if (mLocalVideoTrack != null) {
            mLocalVideoTrack!!.release()
            mLocalVideoTrack = null
        }
        super.onDestroy()
    }

    private fun showHideMenu() {
        if (menuRL!!.visibility == View.VISIBLE)
            menuRL!!.visibility = View.GONE
        else
            menuRL!!.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onClick(v: View) {
        when (v.id) {
            /*case R.id.ll_assistant_online_support:
                menuRL.setVisibility(View.GONE);

                Intent intent1 = new Intent(this, ClassroomAssistantActivity.class);
                intent1.putExtra("fragment", OnlineSupportFragment.class.getSimpleName());
                startActivity(intent1);
                break;*/

            R.id.ll_assistant_faq -> {
                menuRL!!.visibility = View.GONE
                val intent2 = Intent(this, FragmentHolderActivity::class.java)
                val bundle = Bundle()
                bundle.putString(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ClassroomFAQFragment::class.java!!.getSimpleName())
                bundle.putString(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT, ClassRoomActivity::class.java!!.getSimpleName())
                intent2.putExtras(bundle)
                startActivity(intent2)
            }

            R.id.assistant_menu -> showHideMenu()

            R.id.cross_img -> onBackPressed()

            R.id.tv_send_button -> {
                val text = mInputEditText!!.text.toString()
                if (TextUtils.isEmpty(text)) {
                    return
                }
                mServerRequestManager!!.saveClassroomMessage(mBooking!!.bookingId!!, accountId!!, text, object : ServerRequestManager.OnRequestFinishedListener {
                    override fun onSuccess(response: ServerResponse?) {
                        if (response!!.code == 1) {
                            for (remoteParticipant in mRoom!!.remoteParticipants) {
                                val message = android.os.Message.obtain(null, MessengerService.MSG_IM_CLASS)
                                if (remoteParticipant.identity != accountId) {
                                    val bundle = Bundle()
                                    if (remoteParticipant.identity == "T_" + mTutor!!.tutorId!!) {
                                        bundle.putString("send_to", "" + remoteParticipant.identity)
                                    } else {
                                        bundle.putString("send_to", "S_" + remoteParticipant.identity)
                                    }
                                    bundle.putString("message_content", text)
                                    bundle.putString("account_name", accountName)
                                    bundle.putString("classroom_id", mBooking!!.bookingId)
                                    message.data = bundle
                                    try {
                                        mWSMessageClient!!.sendMessage(message)
                                        rvChatMessages!!.smoothScrollToPosition(0)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            }
                            mInputEditText!!.setText("")
                        }
                    }

                    override fun onError(err: ServerError) {
                        mInputEditText!!.setText("")
                    }
                })
            }

            R.id.fab_white_board -> {
                menuRL!!.visibility = View.GONE
                classRoomLayout!!.visibility = View.GONE
                mLocalVideoView!!.visibility = View.GONE
                mRemoteVideoView!!.visibility = View.GONE
                mRlWhiteBoard!!.visibility = View.VISIBLE
                mVideoView!!.visibility = View.VISIBLE

                mRlWhiteBoard!!.removeAllViews()
                mRlWhiteBoard!!.addView(getInstance().webView)

                mVideoView!!.setOnTouchListener { view, motionEvent ->
                    when (motionEvent.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            pressStartTime = System.currentTimeMillis()
                            dX = view.x - motionEvent.rawX
                            dY = view.y - motionEvent.rawY
                            pressX = motionEvent.x
                            pressY = motionEvent.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            distance = distance(pressX, pressY, motionEvent.x, motionEvent.y)
                            if (distance > MAX_CLICK_DISTANCE) {
                                newX = motionEvent.rawX + dX
                                newY = motionEvent.rawY + dY
                                if (newX <= 0 && newY <= 0) {
                                    view.x = 0f
                                    view.y = 0f
                                } else if (newX <= 0 && newY >= screenHight - view.height) {
                                    view.x = 0f
                                    view.y = (screenHight - view.height).toFloat()
                                } else if (newX >= screenWidth - view.width && newY <= 0) {
                                    view.x = (screenWidth - view.width).toFloat()
                                    view.y = 0f
                                } else if (newX >= screenWidth - view.width && newY >= screenHight - view.height) {
                                    view.x = (screenWidth - view.width).toFloat()
                                    view.y = (screenHight - view.height).toFloat()
                                } else if (newX <= 0 && newY >= 0 && newY <= screenHight - view.height) {
                                    view.x = 0f
                                    view.y = newY
                                } else if (newX >= 0 && newX <= screenWidth - view.width && newY <= 0) {
                                    view.x = newX
                                    view.y = 0f
                                } else if (newX >= screenWidth - view.width && newY >= 0 && newY <= screenHight - view.height) {
                                    view.x = (screenWidth - view.width).toFloat()
                                    view.y = newY
                                } else if (newX >= 0 && newX <= screenWidth - view.width && newY >= screenHight - view.height) {
                                    view.x = newX
                                    view.y = (screenHight - view.height).toFloat()
                                } else {
                                    view.x = newX
                                    view.y = newY
                                }
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            Log.i("pressDuration", pressDuration.toString())
                            Log.i("distance", distance.toString())

                            if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE) {
                                classRoomLayout!!.visibility = View.VISIBLE
                                mLocalVideoView!!.visibility = View.VISIBLE
                                mRemoteVideoView!!.visibility = View.VISIBLE
                                mRlWhiteBoard!!.visibility = View.GONE
                                mVideoView!!.visibility = View.GONE
                                menuRL!!.visibility = View.GONE
                            }
                        }
                        else -> return@mVideoView.setOnTouchListener
                        false
                    }
                    true
                }
            }
            /*case R.id.imv_local_video_hide:
                pauseVideo = !pauseVideo;

                pauseVideo(pauseVideo);

                if (pauseVideo) {
                    mLocalVideoHideImageView.setImageDrawable(ContextCompat.getDrawable(ClassRoomActivity.this, R.drawable.ic_video_cam_off));
                } else {
                    mLocalVideoHideImageView.setImageDrawable(ContextCompat.getDrawable(ClassRoomActivity.this, R.drawable.ic_video_cam_off));
                }
                break;*/
            else -> {
            }
        }
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun pauseVideo(pauseVideo: Boolean) {
        if (mLocalVideoTrack != null) {
            mLocalVideoTrack!!.enable(!pauseVideo)
        } else {
            /*if (mLog != null)
                mLog.error("LocalVideoTrack is not present, unable to pause");*/
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return resultCamera == PackageManager.PERMISSION_GRANTED && resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, getString(R.string.cr_camera_permission), Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), CAMERA_MIC_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkPermissionForSDCard(): Boolean {
        val resultReadSD = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val resultWriteSD = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return resultReadSD == PackageManager.PERMISSION_GRANTED && resultWriteSD == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_MIC_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeTwilioSDK()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(
                            this)

                    alertDialogBuilder.setTitle("Permission")
                    alertDialogBuilder.setMessage("Need to check permission in App System Setting")
                    alertDialogBuilder.setPositiveButton("OK") { dialogInterface, i ->
                        dialogInterface.dismiss()
                        finish()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isTalking) {
            val confirm = AlertDialog.Builder(this@ClassRoomActivity)
                    .setTitle(getString(R.string.cr_leave_room))
                    .setMessage(getString(R.string.cr_leave_room_msg))
                    .setPositiveButton(getString(R.string.cr_leave_room_leave)) { dialogInterface, i ->
                        mServerRequestManager!!.leaveClassroom(roomLockID, object : ServerRequestManager.OnRequestFinishedListener {
                            override fun onSuccess(response: ServerResponse?) {
                                if (response!!.code == 1) {
                                    val intent = Intent(applicationContext, TutorFeedbackActivity::class.java)
                                    intent.putExtra(TutorFeedbackActivity.CLASSROOM_ID, mBooking!!.bookingId)
                                    startActivity(intent)

                                    if (isTalking) {
                                        hangUp()
                                        isTalking = false
                                    }
                                    mediaPlayer!!.release()
                                    mediaPlayer = null
                                    this@ClassRoomActivity.finish()
                                }
                            }

                            override fun onError(err: ServerError) {

                            }
                        })
                    }
                    .setNegativeButton(getString(R.string.cr_leave_room_cancel)) { dialogInterface, i -> }
                    .create()
            confirm.show()
        } else {
            hangUp()
        }
    }

    private fun hangUp() {
        if (mLocalParticipant != null)
            mLocalParticipant!!.unpublishTrack(mLocalVideoTrack!!)

        if (mRoom != null)
            mRoom!!.disconnect()

        this@ClassRoomActivity.finish()
    }

    private fun initializeTwilioSDK() {
        mServerRequestManager!!.generateTwilioToken(accountId, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (this@ClassRoomActivity != null) {
                    var videoToken: String? = null
                    try {
                        val jsonObject = JSONObject(result!!.dataSt)
                        videoToken = jsonObject.getString("TokenVideo")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    createAudioAndVideoTracks()
                    if (mLocalAudioTrack != null && mLocalVideoTrack != null)
                        connectToRoom(CommonUtil.generateRoomId(mBooking!!.boardId!!), videoToken)
                }
            }

            override fun onError(err: ServerError) {
                if (this@ClassRoomActivity != null) {
                    AlertDialog.Builder(this@ClassRoomActivity)
                            .setTitle(getString(R.string.error))
                            .setMessage(err.message)
                            .setPositiveButton(getString(R.string.ok), null)
                            .setOnDismissListener { dialogInterface -> onBackPressed() }
                            .create()
                            .show()
                }
            }
        })
    }

    private fun connectToRoom(roomName: String, accessToken: String?) {
        Log.i(TAG, "Classroom Name $roomName Access Token$accessToken")
        configureAudio(true)
        val connectOptionsBuilder = ConnectOptions.Builder(accessToken)
                .roomName(roomName)

        //* Add local audio track to connect options to share with participants.
        if (mLocalAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(listOf<LocalAudioTrack>(mLocalAudioTrack))
        }

        //* Add local video track to connect options to share with participants.
        if (mLocalVideoTrack != null) {
            connectOptionsBuilder.videoTracks(listOf<LocalVideoTrack>(mLocalVideoTrack))
        }
        mRoom = Video.connect(this, connectOptionsBuilder.build(), roomListener())
    }

    private fun configureAudio(enable: Boolean) {
        if (enable) {
            mPreviousAudioMode = mAudioManager!!.mode
            // Request audio focus before making any device switch.
            mAudioManager!!.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            mAudioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION
            //* Always disable microphone mute during a WebRTC call.

            mPreviousMicrophoneMute = mAudioManager!!.isMicrophoneMute
            mAudioManager!!.isMicrophoneMute = false
        } else {
            mAudioManager!!.mode = mPreviousAudioMode
            mAudioManager!!.abandonAudioFocus(null)
            mAudioManager!!.isMicrophoneMute = mPreviousMicrophoneMute
        }
    }

    //* Room events listener


    private fun roomListener(): Room.Listener {
        return object : Room.Listener {
            override fun onConnected(room: Room) {
                mLocalParticipant = room.localParticipant

                if (this@ClassRoomActivity != null) {
                    val message = Message.obtain(null, MessengerService.MSG_SIGNAL)
                    val bundle = Bundle()
                    bundle.putString("tutor_id", "T_" + mBooking!!.tutorId!!)
                    bundle.putString("classroom_id", mBooking!!.bookingId)
                    message.data = bundle
                    try {
                        mWSMessageClient!!.sendMessage(message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    for (participant in room.remoteParticipants) {
                        isTalking = true
                        if (participant.identity == "T_" + mBooking!!.tutorId!!) {
                            addParticipant(participant)
                        }
                        mClassStatusRelativeLayout!!.visibility = View.GONE
                        //break;
                    }

                    val remoteParticipantIterator = room.remoteParticipants.iterator()
                    while (remoteParticipantIterator.hasNext()) {
                        val participant = remoteParticipantIterator.next()
                        isTalking = true
                        if (participant.identity == "T_" + mBooking!!.tutorId!!) {
                            addParticipant(participant)
                        }
                        mClassStatusRelativeLayout!!.visibility = View.GONE
                        break
                    }


                    if (!isTalking) {
                        if (mTutor!!.avatar != null) {
                            mTutorPhotoDraweeView!!.setImageURI(Uri.parse(mTutor!!.avatar))
                        } else {
                            mTutorPhotoDraweeView!!.setImageURI(Uri.parse("res://" + R.drawable.img_pre_tutor))
                        }
                        mClassStatusTextView!!.text = getString(R.string.cr_tutor_soon)
                    }//* Start listening for participant events
                    mServerRequestManager!!.joinClassroom(accountId, mBooking!!.boardId, object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(response: ServerResponse?) {
                            if (response!!.code == 1) {
                                roomLockID = response.dataSt
                            }
                        }

                        override fun onError(err: ServerError) {}
                    })
                }
            }

            override fun onConnectFailure(room: Room, e: TwilioException) {
                configureAudio(false)
            }

            override fun onDisconnected(room: Room, e: TwilioException) {
                mLocalParticipant = null
                this@ClassRoomActivity.mRoom = null
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false)
                }

                if (this@ClassRoomActivity != null) {
                    if (isTalking) {
                        isTalking = false
                        mServerRequestManager!!.leaveClassroom(roomLockID, object : ServerRequestManager.OnRequestFinishedListener {
                            override fun onSuccess(response: ServerResponse?) {
                                if (response!!.code == 1) {
                                    val intent = Intent(this@ClassRoomActivity, TutorFeedbackActivity::class.java)
                                    intent.putExtra(TutorFeedbackActivity.CLASSROOM_ID, mBooking!!.bookingId)
                                    startActivity(intent)
                                    this@ClassRoomActivity.finish()
                                }
                            }

                            override fun onError(err: ServerError) {

                            }
                        })
                    }
                }
            }

            override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
                addParticipant(remoteParticipant)
            }

            override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
                removeParticipant(remoteParticipant)
            }

            override fun onRecordingStarted(room: Room) {
                //* Indicates when media shared to a Room is being recorded. Note that
                //* recording is only available in our Group Rooms developer preview.


                Log.d(TAG, "onRecordingStarted")
            }

            override fun onRecordingStopped(room: Room) {
                //* Indicates when media shared to a Room is no longer being recorded. Note that
                //* recording is only available in our Group Rooms developer preview.


                Log.d(TAG, "onRecordingStopped")
            }
        }
    }

    //* Called when participant joins the room


    private fun addParticipant(participant: RemoteParticipant) {
        if (participant.identity == "T_" + mBooking!!.tutorId!!) {
            tutorParticipant = participant.identity
        }

        isTalking = true

        mClassStatusRelativeLayout!!.visibility = View.GONE

        //* Add participant renderer


        if (participant.remoteVideoTracks.size > 0) {
            val remoteVideoTrackPublication = participant.remoteVideoTracks[0]
            if (remoteVideoTrackPublication.isTrackSubscribed && participant.identity == "T_" + mBooking!!.tutorId!!) {
                addParticipantVideo(remoteVideoTrackPublication.remoteVideoTrack!!)
            }
        }

        participant.setListener(participantListener())
    }

    //* Set primary view as renderer for participant video track


    private fun addParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.addRenderer(mVideoView!!)
        videoTrack.addRenderer(mRemoteVideoView!!)
    }

    //* Called when participant leaves the room


    private fun removeParticipant(participant: RemoteParticipant) {
        if (participant.identity != tutorParticipant) {
            return
        }

        //* Remove participant renderer


        if (participant.remoteVideoTracks.size > 0) {
            val remoteVideoTrackPublication = participant.remoteVideoTracks[0]
            if (remoteVideoTrackPublication.isTrackSubscribed && participant.identity == tutorParticipant) {
                removeParticipantVideo(remoteVideoTrackPublication.remoteVideoTrack!!)
            }
        }

        if (this@ClassRoomActivity != null) {
            if (isTalking) {
                isTalking = false
                mServerRequestManager!!.leaveClassroom(roomLockID, object : ServerRequestManager.OnRequestFinishedListener {
                    override fun onSuccess(response: ServerResponse?) {
                        if (response!!.code == 1) {
                            val intent = Intent(this@ClassRoomActivity, TutorFeedbackActivity::class.java)
                            intent.putExtra(TutorFeedbackActivity.CLASSROOM_ID, mBooking!!.bookingId)
                            startActivity(intent)
                            this@ClassRoomActivity.finish()
                        }
                    }

                    override fun onError(err: ServerError) {}
                })
            }

            //finishActivity(1010);
        }
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeRenderer(mVideoView!!)
        videoTrack.removeRenderer(mRemoteVideoView!!)
    }

    private fun createAudioAndVideoTracks() {
        // Share your microphone
        mLocalAudioTrack = LocalAudioTrack.create(this, true)

        // Share your camera
        mCameraCapturerCompat = CameraCapturerCompat(this, CameraCapturer.CameraSource.FRONT_CAMERA)
        mLocalVideoTrack = LocalVideoTrack.create(this, true, mCameraCapturerCompat!!.videoCapturer!!)
        mLocalVideoView!!.mirror = true
        mLocalVideoTrack!!.addRenderer(mLocalVideoView!!)

        if (mAudioManager != null) {
            mAudioManager!!.isSpeakerphoneOn = true
        }
    }

    private fun participantListener(): RemoteParticipant.Listener {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {

            }

            override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {

            }

            override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, remoteAudioTrack: RemoteAudioTrack) {

            }

            override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, twilioException: TwilioException) {

            }

            override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication, remoteAudioTrack: RemoteAudioTrack) {

            }

            override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }

            override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }

            override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, remoteVideoTrack: RemoteVideoTrack) {
                if (remoteParticipant.identity == "T_" + mBooking!!.tutorId!!) {
                    addParticipantVideo(remoteVideoTrack)
                }
            }

            override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, twilioException: TwilioException) {

            }

            override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication, remoteVideoTrack: RemoteVideoTrack) {
                if (remoteParticipant.identity == tutorParticipant)
                    removeParticipantVideo(remoteVideoTrack)
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

            }

            override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant, remoteAudioTrackPublication: RemoteAudioTrackPublication) {

            }

            override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }

            override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant, remoteVideoTrackPublication: RemoteVideoTrackPublication) {

            }
        }
    }

    //*
    //* Handler of incoming messages from service.


    private inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            val audioPos: Int
            when (msg.what) {
                MessengerService.MSG_IM_CLASS -> {
                    val chatBundle = msg.data

                    val dataString = chatBundle.getString("data")
                    val message = ChatMessage(dataString!!)

                    //Showing Incoming messages
                    if (message.messageType == MessengerService.IM_CLASS || message.messageType == MessengerService.NORMAL_MESSAGE_TYPE) {
                        /*String splitText = message.getSender().split("_")[1];
                        if (accountId.equals(splitText)) {
                            message.setSender(accountName);
                        } else if (mTutor.getTutorId().equals(splitText)) {
                            message.setSender(mTutor.getName());
                        }*/


                        //message.setSender(message.getSenderName());

                        //getting confirmation from group room
                        val splitID = message.sender!!.split("_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                        if (accountId == splitID) {
                            mMessageList.add(message)
                            /*for (RemoteParticipant remoteParticipant : mRoom.getRemoteParticipants()) {
                                if (remoteParticipant.getIdentity().equals(message.getTo().split("_")[1])) {
                                    remoteParticipantCount++;
                                }
                            }
                            if (remoteParticipantCount == mRoom.getRemoteParticipants().size()) {
                                mMessageList.add(message);
                                remoteParticipantCount = 0;
                            }*/
                        } else {
                            mMessageList.add(message)
                        }//getting confirmation from tutor

                        mMessageAdapter!!.notifyDataSetChanged()
                        rvChatMessages!!.smoothScrollToPosition(0)
                    } else if (message.messageType == MessengerService.AUDIO_PLAY_TYPE /*||
                            message.getMessageType().equals(MessengerService.AUDIO_CHANGE_TYPE)*/) {
                        mp3Url = message.body
                        Log.i("mp3URL", mp3Url)
                        try {
                            mediaPlayer!!.reset()
                            mediaPlayer!!.setDataSource(mp3Url)
                            mediaPlayer!!.setVolume(.2f, .2f)
                            mediaPlayer!!.prepare()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        mediaPlayer!!.start()
                    } else if (message.messageType == MessengerService.AUDIO_PAUSE_TYPE) {
                        //audioPos = mediaPlayer.getCurrentPosition();
                        mediaPlayer!!.pause()
                        mediaPlayer!!.reset()
                    } else if (message.messageType == MessengerService.AUDIO_RESUME_TYPE) {
                        Log.i("mp3 resume", mp3Url!! + "")
                        if (mp3Url == null) {
                            val messageContent = message.body
                            try {
                                val msgJson = JSONObject(messageContent)
                                mp3Url = msgJson.getString("audio_url")
                                Log.i("mp3URL", mp3Url)
                                audioPos = (msgJson.getDouble("audio_time") * 1000).toInt()
                                mediaPlayer!!.setDataSource(mp3Url)
                                mediaPlayer!!.setVolume(.2f, .2f)
                                mediaPlayer!!.prepare()
                                mediaPlayer!!.seekTo(audioPos)
                                mediaPlayer!!.start()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } else {
                            val messageContent = message.body
                            try {
                                val msgJson = JSONObject(messageContent)
                                audioPos = (msgJson.getDouble("audio_time") * 1000).toInt()
                                mediaPlayer!!.setDataSource(mp3Url)
                                mediaPlayer!!.setVolume(.2f, .2f)
                                mediaPlayer!!.prepare()
                                mediaPlayer!!.seekTo(audioPos)
                                mediaPlayer!!.start()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    } else if (message.messageType == MessengerService.AUDIO_STOP_TYPE) {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.reset()
                    }//Trigger when Tutor Click 'Stop'
                    //Trigger when Tutor Click 'Resume'
                    //Trigger when Tutor Click 'Pause'
                    //Trigger when Tutor Click 'Play'
                }
                else -> super.handleMessage(msg)
            }//Trigger when Tutor Change MP3
            /*else if (message.getMessageType().equals(MessengerService.AUDIO_CHANGE_TYPE)) {
                    }*///Open Links per receiving links from Tutor by Websocket
            /*else if (message.getMessageType().equals(MessengerService.NETWORK_MATERIAL_TYPE)) {
                        String messageContent = message.getBody();
                        try {
                            JSONObject msgJson = new JSONObject(messageContent);
                            String url = msgJson.getString("message_content");
                            Intent intent = new Intent(ClassRoomActivity.this, FragmentHolderActivity.class);
                            intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, WebviewFragment.class.getSimpleName());
                            intent.putExtra(WebviewFragment.WEBVIEW_EXTRA, url);
                            intent.putExtra(WebviewFragment.TITLE_EXTRA, getString(R.string.classroom));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }*/
        }
    }

    private inner class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(this@ClassRoomActivity).inflate(R.layout.item_message, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var position = position
            val message = mMessageList[mMessageList.size - ++position]

            holder.tvSender.text = message.sender
            holder.tvMessage.text = message.body
        }

        override fun getItemCount(): Int {
            return mMessageList.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvSender: TextView
            var tvMessage: TextView

            init {

                tvSender = itemView.findViewById(R.id.tv_sender)
                tvMessage = itemView.findViewById(R.id.tv_message)
            }
        }
    }

    private inner class EarPhoneRegister : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                    val state = intent.getIntExtra("state", -1)
                    when (state) {
                        0 -> {
                            mAudioManager!!.mode = AudioManager.MODE_IN_CALL
                            if (mAudioManager!!.isSpeakerphoneOn) {
                                mAudioManager!!.isSpeakerphoneOn = true
                            } else {
                                mAudioManager!!.isSpeakerphoneOn = false
                            }
                        }
                        1 -> {
                            mAudioManager!!.mode = AudioManager.MODE_IN_CALL
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

        val EXTRA_BOOKING = "ClassRoomActivity.EXTRA_BOOKING"
        val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
        private val TAG = ClassRoomActivity::class.java!!.getSimpleName()
        private val MAX_CLICK_DURATION = 500
        private val MAX_CLICK_DISTANCE = 5
        private var isTalking: Boolean = false
        private var instance: WebView.WebViewTransport? = null
        private val mMessageList = ArrayList<ChatMessage>()
        private var mMessageAdapter: MessageAdapter? = null
        private var mBooking: Booking? = null
        private var mAccount: Account? = null
        private var mTutor: Tutor? = null
        private var mediaPlayer: MediaPlayer? = null
        private var mp3Url: String? = null
        private val remoteParticipantCount: Int = 0
        private var accountId: String? = null
        private var accountName: String? = null
    }
}
