package inc.osbay.android.tutorroom.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.text.ParseException
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.BookingScheduleAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Booking
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.util.LGCUtil

class ClassScheduleFragment : BackHandledFragment(), BookingScheduleAdapter.OnItemClicked {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.schedule_rv)
    internal var classRV: RecyclerView? = null
    /*@BindView(R.id.previous_date)
    ImageView previousDateImg;
    @BindView(R.id.next_date)
    ImageView nextDateImg;*/
    @BindView(R.id.scheduled_date_tv)
    internal var scheduledDateTV: TextView? = null
    private var selectedDate: String? = null
    private var mBookingList: MutableList<Booking> = ArrayList()
    private var mServerRequestManager: ServerRequestManager? = null
    private val lessonList = ArrayList<Lesson>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbAdapter = DBAdapter(activity)
        selectedDate = arguments.getString("selected_date")
        mBookingList.clear()
        mBookingList = dbAdapter.getFilteredBookings(selectedDate!!)
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.fragment_class_schedule, container, false)
        ButterKnife.bind(this, rootView)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        setTitle(getString(R.string.schedule))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        try {
            scheduledDateTV!!.text = LGCUtil.changeDateFormat(selectedDate!!, LGCUtil.FORMAT_NOTIME, LGCUtil.FORMAT_LONG_WEEKDAY)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val bookingScheduleAdapter = BookingScheduleAdapter(mBookingList, mServerRequestManager, activity, this)
        val mLayoutManager = LinearLayoutManager(activity)
        classRV!!.layoutManager = mLayoutManager
        classRV!!.itemAnimator = DefaultItemAnimator()
        classRV!!.adapter = bookingScheduleAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onItemClick(lessonID: String) {

    }
}
