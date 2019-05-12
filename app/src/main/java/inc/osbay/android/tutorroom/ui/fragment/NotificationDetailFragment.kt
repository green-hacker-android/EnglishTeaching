package inc.osbay.android.tutorroom.ui.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.ParseException
import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Notification
import inc.osbay.android.tutorroom.sdk.util.LGCUtil

class NotificationDetailFragment : BackHandledFragment() {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.title)
    internal var title: TextView? = null
    @BindView(R.id.send_date)
    internal var sendDateTV: TextView? = null
    @BindView(R.id.desc)
    internal var desc: TextView? = null
    private var dbAdapter: DBAdapter? = null
    private var notiID: String? = null
    private var mNotification: Notification? = null
    private var requestManager: ServerRequestManager? = null

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notiID = arguments.getString("noti_id")
        requestManager = ServerRequestManager(activity)
        dbAdapter = DBAdapter(activity)
        mNotification = dbAdapter!!.getNotiById(notiID!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_notification_detail, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        title!!.text = mNotification!!.title
        desc!!.text = mNotification!!.content
        try {
            sendDateTV!!.text = LGCUtil.convertToLocale(mNotification!!.sendDate!!, CommonConstant.DATE_TIME_FORMAT)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestManager!!.readNoti(notiID, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                if (response!!.code == 1) {
                    dbAdapter!!.setNotiRead(notiID!!)
                }
            }

            override fun onError(err: ServerError) {}
        })
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.notification))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}
