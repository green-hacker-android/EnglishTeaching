package inc.osbay.android.tutorroom.service

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.os.*
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.ui.activity.WelcomeActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData
import org.apache.log4j.Logger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.SSLContext

class MessengerService : Service() {

    //    private static final String TUTOR_READY = "ready";
    internal val mMessenger = Messenger(IncomingHandler())
    var notiCount: Int = 0
    /**
     * For showing and hiding our notification.
     */
    internal lateinit var mNM: NotificationManager
    /**
     * Keeps track of all current registered clients.
     */
    internal var mClients = ArrayList<Messenger>()
    internal var mWebSocketClient: WebSocketClient? = null
    internal var mUserId: String? = null
    // sandbox or live
    internal var mMode: String? = null
    internal var isConnected: Boolean = false
    internal lateinit var mTimer: Timer
    internal lateinit var mReceiver: ScreenReceiver
    private val mLog: Logger? = null
    private val messages = HashMap<String, String>()
    private var mMissedHeartBeatCount: Int = 0
    internal var mTimerTask: TimerTask = object : TimerTask() {
        override fun run() {
            val cm = applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val b: Boolean
            if (cm.activeNetworkInfo != null) {
                b = cm.activeNetworkInfo.isConnected
            } else {
                b = false
            }

            if (b) {
                if (!isConnected) {
                    broadcastMessage(Message.obtain(null, MSG_WS_LOGIN, 2, 0))
                    login(mUserId)
                } else {
                    broadcastMessage(Message.obtain(null, MSG_WS_LOGIN, 1, 0))
                    if (mWebSocketClient != null && isConnected) {
                        mMissedHeartBeatCount++
                        if (mMissedHeartBeatCount > 3) {
                            Log.e(TAG, "Missed heart beat count - $mMissedHeartBeatCount")
                        }

                        try {
                            val json = JSONObject()
                            // use at old version.
                            // json.put("message_id", UUID.randomUUID().toString());
                            json.put("account_id", mUserId)
                            json.put("signal_type", HEART_BEAT)
                            json.put("mode", mMode)

                            mWebSocketClient!!.send(json.toString())
                            Log.i(TAG, "Websocket Connected")
                        } catch (e: Exception) {
                            //FlurryAgent.logEvent("Websocket disconnected");
                            Log.e(TAG, "Websocket didn't connected to send message.", e)
                            isConnected = false
                            login(mUserId)
                        }

                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Display a notification about us starting.
        //        showNotification();

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        mReceiver = ScreenReceiver()
        registerReceiver(mReceiver, filter)

        mTimer = Timer()
        mTimer.schedule(mTimerTask, 1, 5000)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            startForeground(1, Notification())
        }
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(123)

        // Tell the user we stopped.
        Toast.makeText(this, "Remote Service Stopped.", Toast.LENGTH_SHORT).show()

        unregisterReceiver(mReceiver)
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        return mMessenger.getBinder()
    }

    /**
     * Show a notification while this service is running.
     */

    private fun showAlertDialog(text: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        val isAppRunning = prefs.getBoolean("is_app_running", false)
        if (isAppRunning) {
            val intent = Intent("InAppAlert")
            intent.putExtra("noti_text", text)
            sendBroadcast(intent)
        } else {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                val alertDialog = AlertDialog.Builder(getApplicationContext())
                        .setTitle("Notice")
                        .setMessage(text)
                        .setPositiveButton("OK", null)
                        .create()

                alertDialog.getWindow()!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)

                alertDialog.show()
            }
        }
    }

    private fun logout() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        val isAppRunning = prefs.getBoolean("is_app_running", false)

        val params = HashMap<String, String>()
        params.put("Last Page", "Kick out by Server")
        //FlurryAgent.logEvent("Close App", params);

        prefs.edit().remove("access_token").apply()
        prefs.edit().remove("account_id").apply()

        mUserId = null
        if (mWebSocketClient != null) {
            mWebSocketClient!!.close()
        }
        isConnected = false
        mWebSocketClient = null

        /*DBAdapter adapter = new DBAdapter(getApplicationContext());
               adapter.deleteAllTableData();*/

        if (isAppRunning) {
            val intent = Intent("Refresh")
            sendBroadcast(intent)
        }
    }

    fun login(userName: String?) {
        var userName = userName
        Log.e(TAG, "login with user name - " + userName!!)

        val prefs = SharedPreferenceData(getApplicationContext())
        val socketUrl = prefs.getString("WebSocketUrl")
        val socketPort = prefs.getString("WebSocketPort")
        //String socketUrl = "ws://192.168.1.22:8005";
        mMode = prefs.getString("WebSocketMode")

        /*if (TextUtils.isEmpty(socketUrl)) {
                   ServerRequestManager requestManager = new ServerRequestManager(getApplicationContext());
                   requestManager.getWebSocketComponent(new ServerRequestManager.OnRequestFinishedListener() {
                       @Override
                       public void onSuccess(Object result) {

                       }

                       @Override
                       public void onError(ServerError err) {

                       }
                   });
               }

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   if (checkPermissionForSDCard()) {
                       Log4jHelper log4jHelper = new Log4jHelper();
                       if (mLog != null)
                           mLog = log4jHelper.getLogger("MessengerService");
                   }
               } else {
                   Log4jHelper log4jHelper = new Log4jHelper();
                   mLog = log4jHelper.getLogger("MessengerService");
               }*/

        if (TextUtils.isEmpty(userName)) {
            userName = "S_" + prefs.getInt("account_id")
            /*String token = prefs.getString("access_token", null);
                       Log.e(TAG, "account id - " + userName + ", token - " + token);

                       if (TextUtils.isEmpty(token)) {
           //                userName = "F_" + Settings.Secure
           //                        .getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                           return;
                       } else {
                           userName = "S_" + userName;
                       }

                       Log.e(TAG, "generated id - " + userName);*/
        }

        if (isConnected && userName == mUserId) {
            return
        }
        mUserId = userName

        Log.e(TAG, "Connect websocket with id - " + mUserId!!)

        val uri: URI
        try {
            uri = URI(socketUrl!! + socketPort!!)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }

        mWebSocketClient = object : WebSocketClient(uri, Draft_6455()) {
            public override fun onOpen(serverHandshake: ServerHandshake) {
                if (mLog != null) {
                    mLog!!.debug("WebSocket Opened. - " + serverHandshake.toString())
                }

                try {
                    val json = JSONObject()
                    json.put("message_id", UUID.randomUUID().toString())
                    json.put("signal_type", INITIATE)
                    json.put("account_id", mUserId)
                    json.put("mode", mMode)

                    if (mLog != null) {
                        mLog!!.debug("Send ws : " + json.toString())
                    }

                    mWebSocketClient!!.send(json.toString())
                } catch (e: Exception) {
                    if (mLog != null) {
                        mLog!!.error("Websocket didn't connected to send message. (Open)" + e.message)
                        //FlurryAgent.logEvent("Websocket disconnected");
                    }
                }

            }

            public override fun onMessage(message: String) {
                try {
                    Log.i("ReceivedMessage", message)
                    val obj = JSONObject(message)
                    val signalType = obj.getString("signal_type")
                    val messageType = obj.getString("message_type")
                    if (HEART_BEAT == signalType) {
                        mMissedHeartBeatCount = 0
                    } else {
                        Log.d(TAG, "Received - " + message)
                        when (signalType) {
                            INITIATE -> {
                                val resultStatus = obj.getInt("status")
                                if (resultStatus == 200) {
                                    if (mLog != null) {
                                        mLog!!.debug("WebSocket Connected.")
                                        //FlurryAgent.logEvent("Websocket connected");
                                    }

                                    isConnected = true
                                    broadcastMessage(Message.obtain(null, MSG_WS_LOGIN, 1, 0))
                                } else {
                                    if (mLog != null) {
                                        mLog!!.debug("WebSocket Disconnected.")
                                        //FlurryAgent.logEvent("Websocket disconnected");
                                    }
                                    mWebSocketClient!!.close()
                                    broadcastMessage(Message.obtain(null, MSG_WS_LOGIN, 2, 0))
                                }
                            }
                            "developer" -> {
                            }
                            "classroom" ->
                                //if (obj.getString("account_id").equals("Server")) {
                                if ((IM_CLASS == messageType
                                                || NORMAL_MESSAGE_TYPE == messageType
                                                || AUDIO_PLAY_TYPE == messageType
                                                || AUDIO_RESUME_TYPE == messageType
                                                || AUDIO_PAUSE_TYPE == messageType
                                                || AUDIO_STOP_TYPE == messageType)) {
                                    val msgobj = messages.get(obj.getString("message_id"))
                                    if (msgobj == null) {
                                        receivedChatMessage(message, MSG_IM_CLASS)
                                    } else {
                                        receivedChatMessage(msgobj, MSG_IM_CLASS)
                                    }
                                } else if ("ready" == messageType) {
                                    if ("Server" != obj.getString("account_id")) {
                                        broadcastMessage(Message.obtain(null, MSG_READY, 0, 0))
                                    }
                                }/*|| AUDIO_CHANGE_TYPE.equals(messageType)
                                        || NETWORK_MATERIAL_TYPE.equals(messageType)*/
                            "onlinesupport" -> {
                                if (NORMAL_MESSAGE_TYPE == messageType) {
                                    val msgobj = messages.get(obj.getString("message_id"))
                                    if (msgobj == null) {
                                        receivedChatMessage(message, MSG_IM_CONSULTANT)
                                    } else {
                                        receivedChatMessage(msgobj, MSG_IM_CONSULTANT)
                                    }
                                }
                                if (TRIAL_MESSAGE_TYPE == messageType || TRIAL_COMFIRM_TYPE == messageType) {
                                    val msgobj = messages.get(obj.getString("message_id"))
                                    if (msgobj == null) {
                                        receivedChatMessage(message, MSG_IM_TRIAL_CONSULTANT)
                                    } else {
                                        receivedChatMessage(msgobj, MSG_IM_TRIAL_CONSULTANT)
                                    }
                                }
                            }
                        }/*if ("StudentResume".equals(messageType)) {
                                    ServerRequestManager requestManager = new ServerRequestManager(getApplicationContext());
                                    requestManager.getStudentInfo(new ServerRequestManager.OnRequestFinishedListener() {
                                        @Override
                                        public void onSuccess(Object result) {
//                                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                            boolean isAppRunning = prefs.getBoolean("is_app_running", false);
//                                            if (isAppRunning) {
                                            Intent intent = new Intent("Refresh");
                                            sendBroadcast(intent);
//                                            }
                                        }

                                        @Override
                                        public void onError(ServerError err) {
                                            // ignore
                                        }
                                    });
                                } else if ("KickOut".equals(messageType)) {
                                    logout();
                                }*///}
                        /*AccountAdapter accountAdapter = new AccountAdapter(getApplicationContext());

                                                       String messageContent;
                                                       try {
                                                           JSONObject msgObj = new JSONObject(obj.getString("message_content"));

                                                           messageContent = String.format(Locale.getDefault(),
                                                                   msgObj.getString("message_template"),
                                                                   LGCUtil.convertToLocale(msgObj.getString("utc_date_time").replace("UTC|", "")));
                                                       } catch (JSONException e) {
                                                           messageContent = obj.getString("message_content");
                                                       }

                                                       LGCNotification lgcNoti = new LGCNotification();
                                                       lgcNoti.setNotiId(obj.getString("notification_history_id"));
                                                       lgcNoti.setType(messageType);
                                                       lgcNoti.setCategory(obj.getString("message_category"));
                                                       lgcNoti.setLevel(obj.getString("priority_level"));
                                                       lgcNoti.setContent(messageContent);
                                                       lgcNoti.setSendDate(LGCUtil.convertToLocale(obj.getString("send_date")));
                                                       lgcNoti.setStatus(0);
                       //// TODO: 11/22/2017 send broadcast
                                                       accountAdapter.insertNotification(lgcNoti);

                                                       Intent intent = new Intent("inc.osbay.android.tutormandarin.NOTIFICATION");
                                                       sendBroadcast(intent);

                                                       if ("UpdateNews".equals(lgcNoti.getType()) ||
                                                               "GlobalBanner".equals(lgcNoti.getType())) {
                                                           showNotification(R.drawable.ic_tutor_mandarin, messageContent);

                                                           if (!Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                                                               if (getApplicationContext() != null) {
                                                                   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                                   boolean isAppNotiOpened = prefs.getBoolean("app_noti_open", false);
                                                                   int notiTotalCount = prefs.getInt("app_noti_count", 0);

                                                                   if (!isAppNotiOpened) {
                                                                       notiTotalCount += 1;
                                                                       notiCount = notiTotalCount;
                                                                       prefs.edit().putInt("app_noti_count", notiCount).apply();
                                                                       ShortcutBadger.applyCount(getApplicationContext(), notiCount);
                                                                   }
                                                               }
                                                           }
                                                       } else {
                                                           showAlertDialog(lgcNoti.getContent());
                                                       }*/
                    }
                } catch (e: JSONException) {
                    if (mLog != null) {
                        mLog!!.error("Couldn't parse message" + e.message)
                    }
                }

            }

            public override fun onClose(i: Int, s: String, b: Boolean) {
                isConnected = false
                mWebSocketClient = null
                if (mLog != null) {
                    mLog!!.debug("WebSocket Closed. " + i + s + b)
                }
            }

            public override fun onError(e: Exception) {
                Log.e(TAG, "WebSocket Error - ", e)
                //FlurryAgent.logEvent("WebSocket Erorr - " + e.getMessage());
                for (i in mClients.indices.reversed()) {
                    try {
                        mClients.get(i).send(Message.obtain(null, MSG_WS_LOGIN, 2, 0))
                    } catch (re: RemoteException) {
                        mClients.removeAt(i)
                    }

                }
            }
        }

        val sslContext: SSLContext

        if (socketUrl.indexOf("wss") == 0) {
            try {
                sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, null, null)
                val factory = sslContext.getSocketFactory()
                mWebSocketClient!!.setSocket(factory.createSocket())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        mWebSocketClient!!.connect()
    }

    private fun broadcastMessage(message: Message) {

        for (i in mClients.indices.reversed()) {
            try {
                mClients.get(i).send(message)
            } catch (e: RemoteException) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.removeAt(i)
            }

        }
    }

    private fun receivedChatMessage(text: String?, msgType: Int) {
        val message = Message.obtain(null, msgType)
        val bundle = Bundle()
        bundle.putString("data", text)
        message.setData(bundle)

        for (i in mClients.indices.reversed()) {
            try {
                mClients.get(i).send(message)
            } catch (e: RemoteException) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.removeAt(i)
            }

        }
    }

    @Throws(JSONException::class)
    private fun sendWSMessage(json: JSONObject) {
        if (!isConnected) {
            if (mLog != null) {
                mLog!!.error("Websocket not connected to send data")
                //FlurryAgent.logEvent("Websocket disconnected");
            }

            isConnected = false
            login(mUserId)
            return
        }

        if (mLog != null) {
            mLog!!.debug("Send ws : " + json.toString())
        }
        messages.put(json.getString("message_id"), json.toString())
        mWebSocketClient!!.send(json.toString())
    }

    public override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        Log.e(TAG, "Task removed")

        val service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                Intent(getApplicationContext(), MessengerService::class.java),
                PendingIntent.FLAG_ONE_SHOT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service)
    }

    private fun checkPermissionForSDCard(): Boolean {
        val resultReadSD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val resultWriteSD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return (resultReadSD == PackageManager.PERMISSION_GRANTED) && (resultWriteSD == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Handler of incoming messages from clients.
     */
    private inner class IncomingHandler : Handler() {
        public override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_CLIENT -> mClients.add(msg.replyTo)
                MSG_UNREGISTER_CLIENT -> mClients.remove(msg.replyTo)
                MSG_WS_LOGIN -> {
                    val userBundle = msg.getData()
                    val userId = userBundle.getString("user_id")
                    login(userId)
                }
                MSG_WS_LOGOUT -> {
                    mUserId = null

                    val prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    prefs.edit().remove("account_id").apply()
                    prefs.edit().remove("access_token").apply()

                    if (mWebSocketClient != null) {
                        mWebSocketClient!!.close()
                    }
                    isConnected = false
                    mWebSocketClient = null
                }
                MSG_IM_CLASS -> {
                    //                    String message = msg.obj.toString();
                    val bundle = msg.getData()

                    val chatMsg = JSONObject()

                    try {
                        chatMsg.put("message_id", UUID.randomUUID().toString())
                        chatMsg.put("signal_type", "classroom")
                        chatMsg.put("message_type", IM_CLASS)
                        chatMsg.put("account_id", mUserId)
                        chatMsg.put("account_name", bundle.getString("account_name"))
                        chatMsg.put("send_to", bundle.getString("send_to"))
                        chatMsg.put("message_content", bundle.getString("message_content"))
                        chatMsg.put("classroom_id", bundle.getString("classroom_id"))
                        chatMsg.put("mode", mMode)

                        sendWSMessage(chatMsg)
                    } catch (e: JSONException) {
                        //                        Log.e(TAG, "Create message fail", e);

                        if (mLog != null) {
                            mLog!!.error("Create message fail" + e.message)
                        }
                    }

                }
                MSG_IM_CONSULTANT -> {
                    //                    String message = msg.obj.toString();
                    val bundle1 = msg.getData()

                    val chatMsg1 = JSONObject()

                    try {
                        chatMsg1.put("message_id", UUID.randomUUID().toString())
                        chatMsg1.put("signal_type", "onlinesupport")
                        chatMsg1.put("message_type", NORMAL_MESSAGE_TYPE)
                        chatMsg1.put("account_id", mUserId)
                        chatMsg1.put("send_to", bundle1.getString("send_to"))
                        chatMsg1.put("message_content", bundle1.getString("message_content"))
                        chatMsg1.put("mode", mMode)

                        sendWSMessage(chatMsg1)
                    } catch (e: JSONException) {
                        if (mLog != null) {
                            mLog!!.error("Create message fail" + e.message)
                        }
                    }

                }
                MSG_IM_TRIAL_CONSULTANT -> {
                    val bundle2 = msg.getData()

                    val chatMsg2 = JSONObject()

                    try {
                        chatMsg2.put("message_id", UUID.randomUUID().toString())
                        chatMsg2.put("signal_type", "onlinesupport")
                        chatMsg2.put("message_type", TRIAL_MESSAGE_TYPE)
                        chatMsg2.put("account_id", mUserId)
                        chatMsg2.put("send_to", bundle2.getString("send_to"))
                        chatMsg2.put("message_content", bundle2.getString("message_content"))
                        chatMsg2.put("mode", mMode)

                        sendWSMessage(chatMsg2)
                    } catch (e: JSONException) {
                        if (mLog != null) {
                            mLog!!.error("Create message fail" + e.message)
                        }
                    }

                }
                MSG_SIGNAL -> {
                    val signal = msg.getData()
                    val studentReady = JSONObject()
                    try {
                        studentReady.put("message_id", UUID.randomUUID().toString())
                        studentReady.put("message_type", "ready")
                        studentReady.put("account_id", mUserId)
                        studentReady.put("signal_type", "classroom")
                        studentReady.put("send_to", signal.getString("tutor_id"))
                        studentReady.put("classroom_id", signal.getString("classroom_id"))
                        studentReady.put("mode", mMode)

                        sendWSMessage(studentReady)
                    } catch (e: JSONException) {
                        if (mLog != null) {
                            mLog!!.error("Create message fail" + e.message)
                        }
                    }

                }
                else -> super.handleMessage(msg)
            }
        }
    }

    internal inner class ScreenReceiver : BroadcastReceiver() {

        public override fun onReceive(context: Context, intent: Intent) {
            //            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //                Log.i(TAG, "Screen Off");
            //            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //                Log.i(TAG, "Screen On");
            //            }
        }

    }

    companion object {

        val MSG_IM_CLASS = 4
        val MSG_REGISTER_CLIENT = 11
        val MSG_UNREGISTER_CLIENT = 12
        val MSG_WS_LOGIN = 13
        val MSG_WS_LOGOUT = 15
        val MSG_SIGNAL = 2
        val MSG_IM_CONSULTANT = 5
        val MSG_IM_TRIAL_CONSULTANT = 3
        val MSG_READY = 14

        val TAG = MessengerService::class.java!!.getSimpleName()

        val IM_CLASS = "im_class"
        val NORMAL_MESSAGE_TYPE = "im_online"
        val INITIATE = "establish"
        val HEART_BEAT = "heartbeat"
        val TRIAL_MESSAGE_TYPE = "trial_request"
        val TRIAL_COMFIRM_TYPE = "trial_confirm"
        val CALL_LOG = "call_msg"
        val AUDIO_PLAY_TYPE = "audio_play"
        val AUDIO_PAUSE_TYPE = "audio_pause"
        val AUDIO_RESUME_TYPE = "audio_resume"
        val AUDIO_STOP_TYPE = "audio_stop"
        val AUDIO_CHANGE_TYPE = "audio_change"
        val NETWORK_MATERIAL_TYPE = "send_network_material"
    }
}