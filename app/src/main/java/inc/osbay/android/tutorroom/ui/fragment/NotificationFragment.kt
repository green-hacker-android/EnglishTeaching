package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.NotificationAdapter
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Notification
import inc.osbay.android.tutorroom.utils.CommonUtil
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class NotificationFragment : BackHandledFragment(), NotificationAdapter.OnItemClicked {


    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.noti_rv)
    internal var notiRV: RecyclerView? = null
    @BindView(R.id.no_noti)
    internal var noNotiTV: TextView? = null
    @BindView(R.id.noti_icon)
    internal var notiIcon: ImageView? = null
    private var dbAdapter: DBAdapter? = null
    private var notificationList: MutableList<Notification> = ArrayList()

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbAdapter = DBAdapter(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onResume() {
        super.onResume()
        val count = dbAdapter!!.notiCount
        notiIcon!!.setImageDrawable(CommonUtil.createImage(activity, count))
        notificationList.clear()
        notificationList = dbAdapter!!.notificationByDateDesc
        if (notificationList.size > 0) {
            notiRV!!.visibility = View.VISIBLE
            noNotiTV!!.visibility = View.GONE
            val notificationAdapter = NotificationAdapter(notificationList, activity, this)
            val mLayoutManager = LinearLayoutManager(activity)
            notiRV!!.layoutManager = mLayoutManager
            notiRV!!.itemAnimator = DefaultItemAnimator()
            notiRV!!.adapter = notificationAdapter
        } else {
            notiRV!!.visibility = View.GONE
            noNotiTV!!.visibility = View.VISIBLE
        }
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

    override fun onItemClick(notiID: String?) {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = NotificationDetailFragment()
        val bundle = Bundle()
        bundle.putString("noti_id", notiID)
        fragment.arguments = bundle
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
    }
}