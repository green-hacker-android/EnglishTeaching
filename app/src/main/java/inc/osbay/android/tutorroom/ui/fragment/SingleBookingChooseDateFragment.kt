package inc.osbay.android.tutorroom.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Fragment
import android.app.FragmentManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

import org.threeten.bp.format.DateTimeFormatter

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class SingleBookingChooseDateFragment : BackHandledFragment(), OnDateSelectedListener {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.calendarView)
    internal var widget: MaterialCalendarView? = null
    @BindView(R.id.schedule_tv)
    internal var scheduleTV: TextView? = null
    private var startHrSt: String? = null
    private var startMinSt: String? = null
    private var currentDateInMili: Long = 0
    private var minBookTimeInMili: Long = 0
    private var classMinuteInMili: Long = 0
    private var minBookTime: Int = 0
    private var maxBookDay: Int = 0
    private var classMinute: Int = 0
    private var lessonID: String? = null
    private var selectedDate: String? = null
    private var startHour: Int = 0
    private var startMin: Int = 0
    private val mHours = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")
    private val mMinutes = arrayOf("00", "30")
    private var lessonType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lessonID = arguments.getString("lesson_id")
        lessonType = arguments.getString("lesson_type")
        val sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        minBookTime = sharedPreferenceData.getInt("min_book_time")
        minBookTimeInMili = (minBookTime * 60 * 1000).toLong()
        maxBookDay = sharedPreferenceData.getInt("max_book_time")
        classMinute = sharedPreferenceData.getInt("class_minute")
        classMinuteInMili = (classMinute * 60 * 1000).toLong()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_single_booking_choose_date, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = Date()
        currentDateInMili = date.time
        val calendar = Calendar.getInstance()
        calendar.time = date
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMin = calendar.get(Calendar.MINUTE)

        calendar.add(Calendar.DAY_OF_YEAR, maxBookDay)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        widget!!.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(CalendarDay.from(year, month, day))
                .commit()
        widget!!.setOnDateChangedListener(this)

        // Adding Minimum book time
        var startHrInt: Int
        var startMinInt: Int
        if (currentMin + minBookTime >= 60) {
            startHrInt = currentHour + (currentMin + minBookTime) / 60
            startMinInt = (currentMin + minBookTime) % 60
        } else {
            startHrInt = currentHour
            startMinInt = currentMin + minBookTime
        }

        // Adding Book time block (30 mins) **/
        if (startMinInt <= 30) {
            startMinInt = 30
        } else {
            startHrInt = startHrInt + 1
            startMinInt = 0
        }

        val endHrInt: Int
        val endMinInt: Int
        if (startMinInt + classMinute >= 60) {
            endHrInt = startHrInt + (startMinInt + classMinute) / 60
            endMinInt = (startMinInt + classMinute) % 60
        } else {
            endHrInt = startHrInt
            endMinInt = startMinInt + classMinute
        }
        showTimeSpinner(startHrInt, startMinInt, endHrInt, endMinInt)
    }

    @SuppressLint("SetTextI18n")
    fun showTimeSpinner(startHour: Int, startMin: Int, endHour: Int, endMin: Int) {
        if (startHour < 10)
            startHrSt = "0$startHour"
        else if (startHour >= 10 && startHour < 24)
            startHrSt = startHour.toString()
        else
            startHrSt = "0" + startHour % 24

        val endHrSt: String
        if (endHour < 10)
            endHrSt = "0$endHour"
        else if (endHour >= 10 && endHour < 24)
            endHrSt = endHour.toString()
        else
            endHrSt = "0" + endHour % 24

        if (startMin < 10)
            startMinSt = "0$startMin"
        else
            startMinSt = startMin.toString()

        val endMinSt: String
        if (endMin < 10)
            endMinSt = "0$endMin"
        else
            endMinSt = endMin.toString()

        this.startHour = startHour
        this.startMin = startMin
        scheduleTV!!.text = "$startHrSt:$startMinSt - $endHrSt:$endMinSt"
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.single_book))
        setDisplayHomeAsUpEnable(true)
    }

    @OnClick(R.id.time_spinner_ll)
    internal fun showSchedule() {
        /*TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (timePicker, startHour, startMinutes) -> {
                    int endHrInt;
                    int endMinInt;
                    if (startMinutes + classMinute >= 60) {
                        endHrInt = startHour + (startMinutes + classMinute) / 60;
                        endMinInt = (startMinutes + classMinute) % 60;
                    } else {
                        endHrInt = startHour;
                        endMinInt = startMinutes + classMinute;
                    }
                    showTimeSpinner(startHour, startMinutes, endHrInt, endMinInt);
                }, startHour, startMin, false);
        timePickerDialog.show();*/

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.timepicker_dialog_layout)
        dialog.setCancelable(false)

        val hourNP = dialog.findViewById<NumberPicker>(R.id.np_book_hour)
        hourNP.minValue = 0
        hourNP.maxValue = 23
        hourNP.displayedValues = mHours

        val minuteNP = dialog.findViewById<NumberPicker>(R.id.np_book_minute)
        minuteNP.displayedValues = null
        minuteNP.minValue = 0
        minuteNP.maxValue = 1
        minuteNP.displayedValues = mMinutes

        setDividerColor(hourNP, activity.resources.getColor(R.color.colorPrimary))
        setDividerColor(minuteNP, activity.resources.getColor(R.color.colorPrimary))

        val okTV = dialog.findViewById<TextView>(R.id.ok_tv)
        val cancelTV = dialog.findViewById<TextView>(R.id.cancel_tv)
        okTV.setOnClickListener { view ->
            dialog.dismiss()
            val startHour: Int
            val startMinutes: Int
            val endHrInt: Int
            val endMinInt: Int
            startHour = Integer.parseInt(mHours[hourNP.value])
            startMinutes = Integer.parseInt(mMinutes[minuteNP.value])

            if (startMinutes + classMinute >= 60) {
                endHrInt = startHour + (startMinutes + classMinute) / 60
                endMinInt = (startMinutes + classMinute) % 60
            } else {
                endHrInt = startHour
                endMinInt = startMinutes + classMinute
            }
            showTimeSpinner(startHour, startMinutes, endHrInt, endMinInt)
        }

        cancelTV.setOnClickListener { view -> dialog.dismiss() }
        dialog.show()
    }

    /*private String getStartTime() {
        return mHours[hourNP.getValue()] + ":" + mMinutes[minuteNP.getValue()];
    }

    private String getEndTime() {
        if (mBookingType == Booking.Type.TOPIC) {
            if (minuteNP.getValue() == 0) {
                return mHours[hourNP.getValue()] + ":25";
            } else {
                return mHours[hourNP.getValue()] + ":55";
            }
        } else {
            if (minuteNP.getValue() == 0) {
                return mHours[hourNP.getValue()] + ":50";
            } else {
                return mHours[(hourNP.getValue() + 1) % mHours.length] + ":20";
            }
        }
    }*/

    private fun setDividerColor(picker: NumberPicker, color: Int) {

        val pickerFields = NumberPicker::class.java!!.getDeclaredFields()
        for (pf in pickerFields) {
            if (pf.getName() == "mSelectionDivider") {
                pf.setAccessible(true)
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    @OnClick(R.id.next_tv)
    internal fun clickNext() {
        if (selectedDate == null)
            Toast.makeText(activity, getString(R.string.select_date_empty), Toast.LENGTH_LONG)
                    .show()
        else {
            val startDateSt = "$selectedDate $startHrSt:$startMinSt:00"
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = sdf.parse(startDateSt)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val selectedDateInMili = date!!.time

            if (selectedDateInMili >= currentDateInMili + minBookTimeInMili) {
                val endDate = Date(selectedDateInMili + classMinuteInMili)

                val endDateSt = sdf.format(endDate)

                val fm = fragmentManager
                val frg = fm.findFragmentById(R.id.framelayout)
                val fragment = SingleBookingChooseTutorFragment()

                val bundle = Bundle()
                bundle.putString("source", SingleBookingChooseDateFragment::class.java!!.getSimpleName())
                bundle.putString("lesson_id", lessonID.toString())
                bundle.putString("lesson_type", lessonType)
                bundle.putString("start_date", startDateSt)
                bundle.putString("end_date", endDateSt)
                fragment.arguments = bundle
                if (frg == null) {
                    fm.beginTransaction()
                            .add(R.id.framelayout, fragment)
                            .addToBackStack(null)
                            .commit()
                } else {
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.framelayout, fragment)
                            .commit()
                }
            } else {
                Toast.makeText(activity, getString(R.string.select_date_err, minBookTime.toString()), Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onDateSelected(
            widget: MaterialCalendarView,
            date: CalendarDay,
            selected: Boolean) {
        selectedDate = if (selected) FORMATTER.format(date.date) else null
    }

    companion object {

        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
