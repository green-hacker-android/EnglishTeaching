package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class Booking : Serializable {

    var bookingId: String? = null
    var bookingType: Int = 0  // 1. Lesson Booking, 2.Package Booking
    var tutorId: String? = null
    var lessonId: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var bookingStatus: Int = 0
    var boardId: String? = null

    constructor() {}

    @Throws(JSONException::class, ParseException::class)
    constructor(json: JSONObject) {
        bookingId = json.getString("class_id")
        boardId = json.getString("board_id")
        startDate = setBookedDateUTC(json.getString("start_date"))
        endDate = setBookedDateUTC(json.getString("end_date"))
        bookingStatus = json.getInt("status")
        lessonId = json.getString("lesson_id")
        bookingType = json.getInt("class_type")
        tutorId = json.getString("tutor_id")
    }

    @Throws(ParseException::class)
    fun setBookedDateUTC(bookedDateUTC: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = dateFormat.parse(bookedDateUTC)

        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(utcDate)
    }

    object Status {
        val ACTIVE = 1
        //public static final int CHANGE = 2;
        val CANCEL = 3
        val FINISH = 4
        val MISS = 5
    }

    object Type {
        val LESSON = 1
        val PACKAGE = 2
    }
}
