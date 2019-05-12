package inc.osbay.android.tutorroom.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Matcher
import java.util.regex.Pattern

import inc.osbay.android.tutorroom.R

object CommonUtil {

    //Draw Notification Count on Canvas
    fun createImage(context: Context, count: Int): Drawable {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.notification)
        val w = bitmap.width
        val h = bitmap.height

        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)

        val paint = Paint()
        paint.color = Color.RED
        c.drawBitmap(bitmap, 0f, 0f, paint)

        val paint1 = Paint()
        paint1.color = Color.WHITE

        val paint2 = Paint()
        paint2.color = ContextCompat.getColor(context, R.color.red)
        paint2.isAntiAlias = true

        val text = count.toString()

        if (text != "0") {
            if (text.length == 1) {
                paint1.textSize = w * 0.25f
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.18f, paint2)
                c.drawText(text, w * 0.63f, h * 0.49f, paint1)
            }

            if (text.length == 2) {
                paint1.textSize = w * 0.25f
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.18f, paint2)
                c.drawText(text, w * 0.57f, h * 0.49f, paint1)
            }

            if (text.length == 3) {
                paint1.textSize = w * 0.22f
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.2f, paint2)
                c.drawText(text, w * 0.525f, h * 0.495f, paint1)
            }
        }
        return BitmapDrawable(context.resources, b)
    }

    //Generate OnlineSupport RoomId
    fun generateRoomId(roomId: String): String {
        val result: String
        val roomName = StringBuilder("Engleezi-")
        val maxcount = 8
        val len = roomId.length
        val zerocount = maxcount - len

        for (i in 0 until zerocount) {
            roomName.append("0")
        }
        result = roomName.toString() + roomId
        return result
    }

    //Generate OnlineSupport RoomId
    fun generateCallSupportRoomId(roomId: String): String {
        val result: String
        val roomName = StringBuilder("Engleezi-Consultant")
        val maxcount = 8
        val len = roomId.length
        val zerocount = maxcount - len

        for (i in 0 until zerocount) {
            roomName.append("0")
        }
        result = roomName.toString() + roomId
        return result
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     *
     * @param email Email
     * @return If email address is correct format, reutrn true.Otherwise, return
     * false.
     */
    fun validateEmail(email: String): Boolean {
        /*if (email.equals("")) {
            return false;
        } else {*/
        val pattern: Pattern
        val matcher: Matcher
        val emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" + "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(emailPattern)
        matcher = pattern.matcher(email)
        return matcher.matches()
        //}
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     *
     * @param password Password
     * @return If email address is correct format, reutrn true.Otherwise, return
     * false.
     */
    fun validatePassword(password: String): Boolean {
        return !TextUtils.isEmpty(password) && password.length >= 6 && password.length <= 10
    }

    fun getFormattedDate(d: Date, format: String, isExt: Boolean): String {
        var endDateStr: String
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        endDateStr = sdf.format(d)

        if (isExt) {
            val sdf1 = SimpleDateFormat("dd", Locale.getDefault())
            when (Integer.parseInt(sdf1.format(d))) {
                1 -> endDateStr += "st"
                2 -> endDateStr += "nd"
                3 -> endDateStr += "rd"
                21 -> endDateStr += "st"
                22 -> endDateStr += "nd"
                23 -> endDateStr += "rd"
                else -> endDateStr += "th"
            }
        }
        return endDateStr
    }

    fun getCustomFormattedDate(d: Date, format: String, b: Boolean): String {
        var dateStr: String

        val sdf = SimpleDateFormat(format, Locale.getDefault())
        dateStr = sdf.format(d)

        val sdf1 = SimpleDateFormat("dd", Locale.getDefault())
        when (Integer.parseInt(sdf1.format(d))) {
            1 -> dateStr += "st"
            2 -> dateStr += "nd"
            3 -> dateStr += "rd"
            21 -> dateStr += "st"
            22 -> dateStr += "nd"
            23 -> dateStr += "rd"
            else -> dateStr += "th"
        }
        if (b) {
            val sdf2 = SimpleDateFormat(" yyyy", Locale.getDefault())
            dateStr += sdf2.format(d)
        } else {
            val sdf2 = SimpleDateFormat(", yyyy", Locale.getDefault())
            dateStr += sdf2.format(d)
        }

        return dateStr
    }

    fun getTimeStringResult(dateStr: Date, formatDate: String): String {
        val sdf = SimpleDateFormat(formatDate, Locale.getDefault())
        return sdf.format(dateStr)
    }

    fun getCustomDateResult(dateStr: String?, requestDate: String, formatDate: String): String {
        val sdf = SimpleDateFormat(requestDate, Locale.getDefault())
        val formatSdf = SimpleDateFormat(formatDate, Locale.getDefault())
        var d: Date? = null
        if (dateStr != null) {
            try {
                d = sdf.parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
        return formatSdf.format(d)
    }

    fun convertStringToDate(d: String, formatDate: String): Date? {
        val sdf = SimpleDateFormat(formatDate, Locale.getDefault())
        try {
            return sdf.parse(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getTodayDateString(d: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(d)
    }

    fun getDateStringFormat(d: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(d)
    }

    @Throws(ParseException::class)
    fun changeLocalDateToUTC(): String {
        val d = Date()
        val bookedDateUTC = getTodayDateString(d)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        val utcDate = dateFormat.parse(bookedDateUTC)

        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(utcDate)
    }

    fun hideKeyBoard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
