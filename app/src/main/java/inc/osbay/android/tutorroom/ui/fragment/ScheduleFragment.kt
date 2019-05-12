package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

import org.json.JSONArray
import org.json.JSONException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Booking
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.utils.EventDecorator
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class ScheduleFragment : BackHandledFragment(), OnDateSelectedListener {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.calendarView)
    internal var calendarView: MaterialCalendarView? = null
    @BindView(R.id.book_new_tv)
    internal var bookTV: TextView? = null
    private var maxBookDay: Int = 0
    private var mServerRequestManager: ServerRequestManager? = null
    private var accountID: String? = null
    private val mBookingList = ArrayList<Booking>()
    private val scheduledLessonDays = ArrayList<CalendarDay>()
    private val missedLessonDays = ArrayList<CalendarDay>()
    private val scheduledTrialDays = ArrayList<CalendarDay>()
    private val scheduledMixedDays = ArrayList<CalendarDay>()
    private val newFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        maxBookDay = sharedPreferenceData.getInt("max_book_time")
        accountID = sharedPreferenceData.getInt("account_id").toString()
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar_schedule, container, false)
        ButterKnife.bind(this, rootView)
        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Setting minimum and maximum date of calendar
    }

    override fun onStart() {
        super.onStart()
        setTitle(getString(R.string.schedule))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        scheduledLessonDays.clear()
        scheduledTrialDays.clear()
        scheduledMixedDays.clear()

        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, maxBookDay)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendarView!!.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(CalendarDay.from(year, month, day))
                .commit()

        mServerRequestManager!!.getBookingList(accountID, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (result!!.code == 1)
                //For Success Situation
                {
                    val dbAdapter = DBAdapter(activity)
                    try {
                        val classArray = JSONArray(result.dataSt)
                        for (i in 0 until classArray.length()) {
                            val booking = Booking(classArray.getJSONObject(i))
                            booking.startDate = LGCUtil.convertUTCToLocale(classArray.getJSONObject(i).getString("start_date"), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_NORMAL)
                            booking.endDate = LGCUtil.convertUTCToLocale(classArray.getJSONObject(i).getString("end_date"), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_NORMAL)
                            mBookingList.add(booking)
                        }
                        dbAdapter.insertBookedClass(mBookingList)
                    } catch (e: JSONException) {
                        Log.e(CommonConstant.TAG, "Cannot parse Booking Object", e)
                    } catch (e: ParseException) {
                        Log.e(CommonConstant.TAG, "Cannot parse Booking Object", e)
                    }

                    //putting background mark on the calendar
                    for (i in mBookingList.indices) {
                        try {
                            val currentDate = LGCUtil.dateToMilisecond(LGCUtil.currentTimeString, LGCUtil.FORMAT_NOTIME)
                            val scheduledDateLong = LGCUtil.dateToMilisecond(LGCUtil.convertToNoTime(mBookingList[i].startDate!!), LGCUtil.FORMAT_NOTIME)

                            if (scheduledDateLong > currentDate || scheduledDateLong == currentDate) {
                                val scheduledDate = LocalDate.parse(LGCUtil.convertToNoTime(mBookingList[i].startDate!!))
                                val scheduledCalendarDay = CalendarDay.from(scheduledDate)
                                if (mBookingList[i].bookingType == CommonConstant.Single)
                                //1 = Single Booking
                                    scheduledLessonDays.add(scheduledCalendarDay)
                                else if (mBookingList[i].bookingType == CommonConstant.Trial)
                                //4 = Trial booking
                                    scheduledTrialDays.add(scheduledCalendarDay)
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }

                    calendarView!!.addDecorator(EventDecorator(CommonConstant.Single, Color.RED, scheduledLessonDays, activity))
                    calendarView!!.addDecorator(EventDecorator(CommonConstant.Trial, Color.RED, scheduledTrialDays, activity))

                    for (i in scheduledLessonDays.indices) {
                        for (j in scheduledTrialDays.indices) {
                            if (scheduledTrialDays[j] == scheduledLessonDays[i])
                                scheduledMixedDays.add(scheduledTrialDays[j])
                        }
                    }

                    calendarView!!.addDecorator(EventDecorator(CommonConstant.Single_Trial, Color.RED, scheduledMixedDays, activity))
                }
            }

            override fun onError(err: ServerError) {
                Log.i("Get Booking List Error", err.message)
            }
        })
        calendarView!!.setOnDateChangedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    @OnClick(R.id.book_new_tv)
    internal fun bookNewLesson() {
        val newFragment = SingleBookingChooseLessonFragment()
        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)

        val bundle = Bundle()
        bundle.putString(SingleBookingChooseLessonFragment.Booking_EXTRA, ScheduleFragment::class.java!!.getSimpleName())
        bundle.putString("lesson_type", CommonConstant.singleLessonType.toString())
        newFragment.arguments = bundle

        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, newFragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, newFragment)
                    .commit()
        }
    }

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onDateSelected(materialCalendarView: MaterialCalendarView,
                                calendarDay: CalendarDay, b: Boolean) {
        val selectedDate = if (b) FORMATTER.format(calendarDay.date) else null
        if (mBookingList != null) {
            for (i in mBookingList.indices) {
                try {
                    assert(selectedDate != null)
                    if (selectedDate == LGCUtil.convertToNoTime(mBookingList[i].startDate!!)) {
                        val fm = fragmentManager
                        val fragment = fm.findFragmentById(R.id.framelayout)
                        val newFragment = ClassScheduleFragment()

                        val bundle = Bundle()
                        bundle.putString("selected_date", selectedDate)
                        newFragment.arguments = bundle
                        if (fragment == null) {
                            fm.beginTransaction()
                                    .add(R.id.framelayout, newFragment)
                                    .commit()
                        } else {
                            fm.beginTransaction()
                                    .replace(R.id.framelayout, newFragment)
                                    .addToBackStack(null)
                                    .commit()
                        }
                        break
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        }
    }

    companion object {

        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
