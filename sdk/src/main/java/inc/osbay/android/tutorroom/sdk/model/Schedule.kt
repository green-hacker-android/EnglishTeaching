package inc.osbay.android.tutorroom.sdk.model

import android.util.Log

import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class Schedule {
    var scheduleId: String? = null
    var tutorId: String? = null
    var startTime: String? = null
    var endTime: String? = null

    var startTimeUTC: String
        @Throws(ParseException::class)
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getDefault()
            val localDate = dateFormat.parse(startTime)

            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.format(localDate)
        }
        @Throws(ParseException::class)
        set(startTimeUTC) {
            Log.e("Schedule Model", "start time utc - $startTimeUTC")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = dateFormat.parse(startTimeUTC)

            dateFormat.timeZone = TimeZone.getDefault()
            startTime = dateFormat.format(utcDate)

            Log.e("Schedule Model", "start time local - " + startTime!!)
        }

    var endTimeUTC: String
        @Throws(ParseException::class)
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getDefault()
            val localDate = dateFormat.parse(endTime)

            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.format(localDate)
        }
        @Throws(ParseException::class)
        set(endTimeUTC) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = dateFormat.parse(endTimeUTC)

            dateFormat.timeZone = TimeZone.getDefault()
            endTime = dateFormat.format(utcDate)
        }

    constructor() {}

    @Throws(JSONException::class, ParseException::class)
    constructor(tutorId: String, jsonObject: JSONObject) {
        this.tutorId = tutorId
        scheduleId = jsonObject.getString("schedule_id")
        startTimeUTC = jsonObject.getString("start_date")
        endTimeUTC = jsonObject.getString("end_date")
    }
}
