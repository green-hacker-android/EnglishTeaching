package inc.osbay.android.tutorroom.ui.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Messenger
import android.os.RemoteException
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.TRApplication
import inc.osbay.android.tutorroom.adapter.OnlineSupportMessageAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.model.ChatMessage
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.service.MessengerService
import inc.osbay.android.tutorroom.ui.activity.CallSupportActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData
import inc.osbay.android.tutorroom.utils.WSMessageClient

import com.facebook.FacebookSdk.getApplicationContext

class OnlineSupportFragment : BackHandledFragment() {
    internal var sharedPreferences: SharedPreferenceData
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.rv_chat_messages)
    internal var messageRV: RecyclerView? = null
    @BindView(R.id.edt_input_text)
    internal var inputET: EditText? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var mWSMessageClient: WSMessageClient? = null
    private var accountId: String? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mConsultantId: String? = null
    private var mCallMenuItem: MenuItem? = null
    private var mTimer: Timer? = null
    private var mMessenger: Messenger? = null

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = SharedPreferenceData(activity)
        accountId = sharedPreferences.getInt("account_id").toString()
        mServerRequestManager = ServerRequestManager(activity.applicationContext)

        mContext = activity
        mMessenger = Messenger(IncomingHandler())
        mWSMessageClient = (activity.application as TRApplication).wsMessageClient
        mWSMessageClient!!.addMessenger(mMessenger)

        mProgressDialog = ProgressDialog(activity)
        mProgressDialog!!.setMessage(getString(R.string.loading))
        mProgressDialog!!.setCancelable(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_online_support, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mServerRequestManager!!.getOnlineCallCentreManager(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                mProgressDialog!!.dismiss()
                if (result!!.code == 1) {
                    try {
                        val dataArray = JSONArray(result.dataSt)
                        for (i in 0 until dataArray.length()) {
                            val jsonObject = dataArray.getJSONObject(i)
                            mConsultantId = jsonObject.getString("id")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    getMessages()
                }
            }

            override fun onError(err: ServerError) {
                mProgressDialog!!.dismiss()
                Log.i("Online Support Error", err.message)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    if (mCallMenuItem != null) {
                        if (CallSupportActivity.sIsTalking) {
                            mCallMenuItem!!.setIcon(R.mipmap.ic_calling)
                        } else {
                            mCallMenuItem!!.setIcon(R.mipmap.ic_call)
                        }
                    }
                }
            }
        }, 1000, 1000)
        //}
    }

    override fun onPause() {
        super.onPause()
        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        mTimer!!.cancel()
        //}
    }

    private fun getMessages() {
        val chatMessage = ChatMessage()
        mServerRequestManager!!.getMessages(mConsultantId, accountId, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                mProgressDialog!!.dismiss()
                showIntroMsg(chatMessage)
                if (result!!.code == 1) {
                    val messages = ArrayList<ChatMessage>()
                    var results: JSONArray? = null
                    try {
                        results = JSONArray(result.dataSt)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    try {
                        for (i in 0 until results!!.length()) {
                            val direction = results!!.getJSONObject(i).getInt("direction")
                            // 1. call fail, 2. call success
                            val isRead = results.getJSONObject(i).getInt("isread")
                            val sendDate = results.getJSONObject(i).getString("send_date")
                            var msgContent = results.getJSONObject(i).getString("message_content")
                            val msgId = results.getJSONObject(i).getString("message_id")
                            val callCenterName = results.getJSONObject(i).getString("call_center_id")

                            val msg = ChatMessage()

                            if (direction == 1) {
                                msg.sender = "S_" + accountId!!
                                setMessages(messages, msgContent, msgId, msg)
                            } else if (direction == 0) {
                                msg.sender = "A_auto"
                                if (isRead == 2) {
                                    msgContent = mContext!!.getString(inc.osbay.android.tutorroom.sdk.R.string.os_call_support_success, callCenterName, LGCUtil.convertToLocale(sendDate, CommonConstant.DATE_TIME_FORMAT))
                                } else {
                                    msgContent = mContext!!.getString(inc.osbay.android.tutorroom.sdk.R.string.os_call_support_fail, callCenterName, LGCUtil.convertToLocale(sendDate, CommonConstant.DATE_TIME_FORMAT))
                                }
                                setMessages(messages, msgContent, msgId, msg)

                            } else {
                                msg.sender = "A_auto"
                                setMessages(messages, msgContent, msgId, msg)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }
                mOnlineSupportMessageAdapter = OnlineSupportMessageAdapter(mChatMessages, activity, accountId)
                val layoutManager = LinearLayoutManager(activity)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true
                messageRV!!.layoutManager = layoutManager
                messageRV!!.adapter = mOnlineSupportMessageAdapter
                mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
                messageRV!!.scrollToPosition(0)
            }

            override fun onError(err: ServerError) {
                showIntroMsg(chatMessage)
                mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
                mProgressDialog!!.dismiss()
                Log.i("Online Support Error", err.message)
            }
        })
    }

    private fun setMessages(messages: List<ChatMessage>, msgContent: String, msgId: String, msg: ChatMessage) {
        msg.body = msgContent
        //msg.setMessageType(messageType);
        msg.messageId = msgId
        mChatMessages.add(msg)
    }

    @OnClick(R.id.tv_send_button)
    internal fun send() {
        val text = inputET!!.text.toString()
        if (TextUtils.isEmpty(text)) {
            return
        }

        messageRV!!.smoothScrollToPosition(0)
        /*mChatMessages.add(text);
        mOnlineSupportMessageAdapter.notifyDataSetChanged();*/

        val message: android.os.Message
        val bundle = Bundle()
        mServerRequestManager!!.saveOnlineSupportMessage(mConsultantId, accountId, text, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {}

            override fun onError(err: ServerError) {}
        })
        message = android.os.Message.obtain(null, MessengerService.MSG_IM_CONSULTANT)
        bundle.putString("send_to", "A_" + mConsultantId!!)
        bundle.putString("message_content", text)
        message.data = bundle
        try {
            mWSMessageClient!!.sendMessage(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        inputET!!.setText("")
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.online_support))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.opt_call_support -> if (!CallSupportActivity.sIsTalking) {
                AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.os_call_ready))
                        .setMessage(getString(R.string.os_call_ready_msg))
                        .setPositiveButton(getString(R.string.confirm)) { dialogInterface, i ->
                            mProgressDialog!!.show()
                            initializeTwilioSDK()
                        }
                        .setNegativeButton(getString(R.string.cr_leave_room_cancel), null)
                        .create()
                        .show()

            } else {
                val callIntent = Intent(activity, CallSupportActivity::class.java)
                startActivity(callIntent)
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Disabling call function for Trial Request
        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        inflater.inflate(R.menu.menu_support_call, menu)
        mCallMenuItem = menu.findItem(R.id.opt_call_support)
        //}
    }

    override fun onDestroy() {
        mWSMessageClient!!.removeMessenger(mMessenger)
        mChatMessages.clear()
        super.onDestroy()
    }

    private fun initializeTwilioSDK() {
        mServerRequestManager!!.generateTwilioToken(accountId, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                mProgressDialog!!.dismiss()
                if (activity != null && result!!.code == 1) {
                    try {
                        val jsonObject = JSONObject(result.dataSt)
                        val videoToken = jsonObject.getString("TokenVideo")
                        val intent = Intent(activity, CallSupportActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra(CallSupportActivity.EXTRA_CONSULTANT_ID, mConsultantId)
                        intent.putExtra(CallSupportActivity.EXTRA_ACCESS_TOKEN, videoToken)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onError(err: ServerError) {
                mProgressDialog!!.dismiss()

                if (activity != null) {
                    AlertDialog.Builder(activity)
                            .setTitle(getString(R.string.error))
                            .setMessage(err.message)
                            .setPositiveButton(getString(R.string.ok), null)
                            .create()
                            .show()
                }
            }
        })
    }

    private fun showIntroMsg(chatMessage: ChatMessage) {
        /*if (classType.equalsIgnoreCase("trial")) {
            chatMessage.setBody(getString(R.string.trial_request_welcome));
            chatMessage.setMessageType("trial_request");
        } else {*/
        chatMessage.body = getString(R.string.os_msg_body)
        chatMessage.messageType = "im_online"
        //}
        chatMessage.sender = "A_auto"
        mChatMessages.add(chatMessage)
    }

    /**
     * Handler of incoming messages from service.
     */
    private inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                MessengerService.MSG_IM_CONSULTANT -> {
                    val chatBundle = msg.data

                    val message = ChatMessage(chatBundle.getString("data")!!)

                    mChatMessages.add(message)
                    mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
                    messageRV!!.smoothScrollToPosition(0)

                    /*** C_ is for Call Center Manager  */
                    mServerRequestManager!!.updateMessageStatus(mConsultantId, accountId, object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(response: ServerResponse?) {

                        }

                        override fun onError(err: ServerError) {

                        }
                    })
                }
                MessengerService.MSG_IM_TRIAL_CONSULTANT -> {
                    val chatBundle1 = msg.data

                    val message1 = ChatMessage(chatBundle1.getString("data")!!)

                    /*** G_ is for Consultant  */
                    /*if (message1.getSender().startsWith("G_")) {
                        mServerRequestManager.updateMessageHistoryConsultant(new ServerRequestManager.OnRequestFinishedListener() {
                            @Override
                            public void onSuccess(ServerResponse result) {
                                mServerRequestManager.getStudentInfo(new ServerRequestManager.OnRequestFinishedListener() {
                                    @Override
                                    public void onSuccess(ServerResponse result) {
                                        if (getActivity() != null) {
                                        }
                                    }

                                    @Override
                                    public void onError(ServerError err) {
                                        if (getActivity() != null) {
                                            Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(ServerError err) {
                            }
                        });
                    }*/

                    mChatMessages.add(message1)
                    mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
                    messageRV!!.smoothScrollToPosition(0)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    companion object {

        var mContext: Context? = null
        private var mOnlineSupportMessageAdapter: OnlineSupportMessageAdapter? = null
        private val mChatMessages = ArrayList<ChatMessage>()

        fun showError(errCode: Int) {
            //trigger when Call Center Manager isn't online
            if (errCode == 106) {
                val chatMessage = ChatMessage()
                if (mContext != null)
                    chatMessage.body = mContext!!.getString(R.string.os_no_consultants)
                else
                    chatMessage.body = "Sorry, Engleezi consultants are not available to take your " +
                            "call right now. Please leave a written message in the window and we'll " +
                            "get back to you as soon as we can."
                chatMessage.sender = "C_auto"
                chatMessage.messageType = "im_online"
                mChatMessages.add(chatMessage)

                mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
            }
            if (errCode == 107) {
                val chatMessage = ChatMessage()
                if (mContext != null)
                    chatMessage.body = mContext!!.getString(R.string.os_call_ended)
                else
                    chatMessage.body = "The call was ended."

                chatMessage.sender = "C_auto"
                chatMessage.messageType = "im_online"
                mChatMessages.add(chatMessage)

                mOnlineSupportMessageAdapter!!.notifyDataSetChanged()
            }
        }
    }
}
