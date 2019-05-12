package inc.osbay.android.tutorroom.sdk.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import java.util.ArrayList

import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.Booking
import inc.osbay.android.tutorroom.sdk.model.CountryCode
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.LessonDuration
import inc.osbay.android.tutorroom.sdk.model.Notification
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.sdk.util.LGCUtil

class DBAdapter(context: Context) {

    private val mDbHelper: DataBaseHelper

    /*** Get Mobile Country Code  */
    val countryCodes: List<CountryCode>
        get() {
            val countryCodes = ArrayList<CountryCode>()
            val db = mDbHelper.readableDatabase
            val sql = "SELECT * FROM country_code WHERE phone_code > 0 ORDER BY nice_name"
            val mCur = db.rawQuery(sql, null)
            try {
                if (mCur.moveToFirst()) {
                    do {
                        val code = CountryCode()

                        code.codeId = mCur.getInt(mCur.getColumnIndex("code_id"))
                        code.country = mCur.getString(mCur.getColumnIndex("nice_name"))
                        code.code = mCur.getInt(mCur.getColumnIndex("phone_code"))

                        countryCodes.add(code)
                    } while (mCur.moveToNext())

                    return countryCodes
                }
            } finally {
                mCur.close()
                db.close()
            }
            return countryCodes
        }

    /*** Get Company ID  */
    val companyID: String?
        get() {
            var companyID: String? = null
            val db = mDbHelper.readableDatabase
            val sql = "SELECT * FROM company WHERE company_name='Engleezi'"
            val cursor = db.rawQuery(sql, null)
            try {
                if (cursor.moveToFirst()) {
                    companyID = cursor.getString(cursor.getColumnIndex("id"))
                }
            } finally {
                cursor.close()
                db.close()
            }
            return companyID
        }

    /**
     * Get Lesson List
     */
    val lessonList: List<Lesson>
        get() {
            val lessonList = ArrayList<Lesson>()
            val db = mDbHelper.readableDatabase

            val sql = "SELECT * FROM lesson"
            val cursor = db.rawQuery(sql, null)

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val b = Lesson()
                        b.lessonId = cursor.getString(cursor.getColumnIndex("lesson_id"))
                        b.lessonName = cursor.getString(cursor.getColumnIndex("lesson_name"))
                        b.lessonDescription = cursor.getString(cursor.getColumnIndex("lesson_description"))
                        b.lessonPrice = cursor.getDouble(cursor.getColumnIndex("lesson_price"))
                        b.lessonPath = cursor.getString(cursor.getColumnIndex("lesson_path"))
                        b.lessonCover = cursor.getString(cursor.getColumnIndex("lesson_cover"))
                        b.classMin = cursor.getInt(cursor.getColumnIndex("class_min"))
                        lessonList.add(b)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bookings update fail.", e)
            } finally {
                cursor.close()
                db.close()
            }
            return lessonList
        }

    /*** Get All Booked Class  */
    val allBookings: List<Booking>
        get() {
            val bookings = ArrayList<Booking>()
            val db = mDbHelper.readableDatabase

            val sql = "SELECT * FROM booking ORDER BY start_date ASC"
            val cursor = db.rawQuery(sql, null)
            try {
                if (cursor.moveToFirst()) {
                    do {
                        val b = Booking()
                        b.bookingId = cursor.getString(cursor.getColumnIndex("class_id"))
                        b.boardId = cursor.getString(cursor.getColumnIndex("board_id"))
                        b.startDate = cursor.getString(cursor.getColumnIndex("start_date"))
                        b.endDate = cursor.getString(cursor.getColumnIndex("end_date"))
                        b.bookingStatus = cursor.getInt(cursor.getColumnIndex("status"))
                        b.lessonId = cursor.getString(cursor.getColumnIndex("lesson_id"))
                        b.bookingType = cursor.getInt(cursor.getColumnIndex("class_type"))
                        b.tutorId = cursor.getString(cursor.getColumnIndex("tutor_id"))
                        bookings.add(b)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bookings update fail.", e)
            } finally {
                cursor.close()
                db.close()
            }
            return bookings
        }

    val notificationByDateDesc: List<Notification>
        get() {
            val notiList = ArrayList<Notification>()
            val db = mDbHelper.readableDatabase

            val sql = "SELECT * FROM notification ORDER BY send_date DESC"

            val mCur = db.rawQuery(sql, null)
            try {
                if (mCur.moveToFirst()) {
                    do {
                        val noti = Notification()
                        noti.notiId = mCur.getString(mCur.getColumnIndex("noti_id"))
                        noti.type = mCur.getString(mCur.getColumnIndex("type"))
                        noti.title = mCur.getString(mCur.getColumnIndex("title"))
                        noti.comment = mCur.getString(mCur.getColumnIndex("comment"))
                        noti.content = mCur.getString(mCur.getColumnIndex("content"))
                        noti.sendDate = mCur.getString(mCur.getColumnIndex("send_date"))
                        noti.status = mCur.getInt(mCur.getColumnIndex("status"))
                        notiList.add(noti)
                    } while (mCur.moveToNext())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cannot parse notification object.")
            } finally {
                mCur.close()
                db.close()
            }
            return notiList
        }

    /*** Get Noti Count (return integer value)  */
    val notiCount: Int
        get() {
            val countQuery = "SELECT * FROM notification WHERE status = ?"
            val db = mDbHelper.readableDatabase

            val args = arrayOf("1")
            val cursor = db.rawQuery(countQuery, args)
            val cnt = cursor.count
            cursor.close()
            return cnt
        }

    /*** Get Tutor List  */
    val allTutor: List<Tutor>
        get() {
            val tutors = ArrayList<Tutor>()
            val db = mDbHelper.readableDatabase
            try {
                val sql = "SELECT * FROM tutor"

                val mCur = db.rawQuery(sql, null)
                if (mCur.moveToFirst()) {
                    do {
                        val tutor = Tutor()

                        tutor.tutorId = mCur.getString(mCur.getColumnIndex("tutor_id"))
                        tutor.name = mCur.getString(mCur.getColumnIndex("name"))
                        tutor.rate = mCur.getString(mCur.getColumnIndex("rate"))
                        tutor.teachingExp = mCur.getString(mCur.getColumnIndex("experience"))
                        tutor.creditWeight = mCur.getString(mCur.getColumnIndex("credit_weight"))
                        tutor.avatar = mCur.getString(mCur.getColumnIndex("avatar"))
                        tutor.introVoice = mCur.getString(mCur.getColumnIndex("intro_voice"))
                        tutor.location = mCur.getString(mCur.getColumnIndex("location"))

                        tutors.add(tutor)
                    } while (mCur.moveToNext())
                }
                mCur.close()
            } catch (mSQLException: SQLException) {
                Log.e(TAG, "getTestData >>$mSQLException")
            } finally {
                db.close()
            }
            return tutors
        }

    val lessonDuration: List<LessonDuration>
        get() {
            val durations = ArrayList<LessonDuration>()
            val db = mDbHelper.readableDatabase
            val sql = "SELECT * FROM lesson_duration"
            val mCur = db.rawQuery(sql, null)
            try {
                if (mCur.moveToFirst()) {
                    do {
                        val duration = LessonDuration()

                        duration.id = mCur.getString(mCur.getColumnIndex("lesson_id"))
                        duration.startHour = mCur.getInt(mCur.getColumnIndex("start_hour"))
                        duration.startMin = mCur.getInt(mCur.getColumnIndex("start_min"))
                        duration.endHour = mCur.getInt(mCur.getColumnIndex("end_hour"))
                        duration.endMin = mCur.getInt(mCur.getColumnIndex("end_min"))

                        durations.add(duration)
                    } while (mCur.moveToNext())

                    return durations
                }
            } finally {
                mCur.close()
                db.close()
            }
            return durations
        }

    init {
        mDbHelper = DataBaseHelper(context)
    }

    /*** Insert Lessons  */
    fun insertLessons(newLessons: List<Lesson>) {
        val db = mDbHelper.writableDatabase
        try {
            db.beginTransaction()
            //db.delete("lesson", null, null);
            for (newLesson in newLessons) {
                val lessonCount = getLessonCountByID(newLesson.lessonId)
                val values = ContentValues()
                values.put("lesson_id", newLesson.lessonId)
                values.put("lesson_name", newLesson.lessonName)
                values.put("lesson_description", newLesson.lessonDescription)
                values.put("lesson_price", newLesson.lessonPrice)
                values.put("lesson_path", newLesson.lessonPath)
                values.put("lesson_cover", newLesson.lessonCover)
                values.put("class_min", newLesson.classMin)
                if (lessonCount == 0) {
                    db.insert("lesson", null, values)
                } else {
                    db.replace("lesson", null, values)
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot insert lessons.", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /**
     * Get Lesson List
     */
    fun getLessonByID(lessonID: String): Lesson {
        val db = mDbHelper.readableDatabase
        val b = Lesson()

        val selectionArgs = arrayOf(lessonID)
        val sql = "SELECT * FROM lesson WHERE lesson_id = ?"
        val cursor = db.rawQuery(sql, selectionArgs)
        try {
            if (cursor.moveToFirst()) {
                do {
                    b.lessonId = cursor.getString(cursor.getColumnIndex("lesson_id"))
                    b.lessonName = cursor.getString(cursor.getColumnIndex("lesson_name"))
                    b.lessonDescription = cursor.getString(cursor.getColumnIndex("lesson_description"))
                    b.lessonPrice = cursor.getDouble(cursor.getColumnIndex("lesson_price"))
                    b.lessonPath = cursor.getString(cursor.getColumnIndex("lesson_path"))
                    b.lessonCover = cursor.getString(cursor.getColumnIndex("lesson_cover"))
                    b.classMin = cursor.getInt(cursor.getColumnIndex("class_min"))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Bookings update fail.", e)
        } finally {
            cursor.close()
            db.close()
        }
        return b
    }

    /*** Get Lesson Count (return integer value)  */
    fun getLessonCountByID(lessonID: String?): Int {
        val countQuery = "SELECT * FROM lesson WHERE lesson_id = ?"
        val db = mDbHelper.readableDatabase

        val args = arrayOf<String>(lessonID!!)
        val cursor = db.rawQuery(countQuery, args)
        val cnt = cursor.count
        cursor.close()
        return cnt
    }

    /*** Insert Booked Class  */
    fun insertBookedClass(bookingList: List<Booking>) {
        val db = mDbHelper.writableDatabase
        try {
            db.beginTransaction()
            db.delete("booking", null, null)
            for (booking in bookingList) {
                val values = ContentValues()
                values.put("class_id", booking.bookingId)
                values.put("board_id", booking.boardId)
                values.put("start_date", booking.startDate)
                values.put("end_date", booking.endDate)
                values.put("status", booking.bookingStatus)
                values.put("tutor_id", booking.tutorId)
                values.put("lesson_id", booking.lessonId)
                values.put("class_type", booking.bookingType)
                db.insert("booking", null, values)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot insert bookings.", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /*** Get Filtered Booked Class  */
    fun getFilteredBookings(selectedDate: String): List<Booking> {
        val bookings = ArrayList<Booking>()
        val db = mDbHelper.readableDatabase

        val sql = "SELECT * FROM booking ORDER BY start_date ASC"
        val cursor = db.rawQuery(sql, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (selectedDate == LGCUtil.convertToNoTime(cursor.getString(cursor.getColumnIndex("start_date")))) {
                        val b = Booking()
                        b.bookingId = cursor.getString(cursor.getColumnIndex("class_id"))
                        b.boardId = cursor.getString(cursor.getColumnIndex("board_id"))
                        b.startDate = cursor.getString(cursor.getColumnIndex("start_date"))
                        b.endDate = cursor.getString(cursor.getColumnIndex("end_date"))
                        b.bookingStatus = cursor.getInt(cursor.getColumnIndex("status"))
                        b.lessonId = cursor.getString(cursor.getColumnIndex("lesson_id"))
                        b.bookingType = cursor.getInt(cursor.getColumnIndex("class_type"))
                        b.tutorId = cursor.getString(cursor.getColumnIndex("tutor_id"))
                        bookings.add(b)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Bookings update fail.", e)
        } finally {
            cursor.close()
            db.close()
        }
        return bookings
    }

    /*** Delete All Tables  */
    fun deleteAllTableData() {
        val db = mDbHelper.readableDatabase
        db.delete("account", null, null)
        //db.delete("badges", null, null);
        db.delete("booking", null, null)
        db.delete("country", null, null)
        db.delete("lesson", null, null)
        /* db.delete("dialog", null, null);
        db.delete("flashcard", null, null);
        db.delete("flashcard_deck", null, null);
        db.delete("lesson", null, null);
        db.delete("note", null, null);*/
        db.delete("notification", null, null)
        //db.delete("package", null, null);
        db.delete("tutor", null, null)
        /*db.delete("tutor_schedules", null, null);
        db.delete("video", null, null);
        db.delete("whatson", null, null);
        db.delete("whats_on_vocab", null, null);
        db.delete("topic", null, null);
        db.delete("topic_class", null, null);
        db.delete("trial_class", null, null);
        db.delete("promo_code_referrer", null, null);
        db.delete("promo_code_enter", null, null);*/
    }

    /*** Insert Notifications  */
    fun insertNotifications(notiList: List<Notification>) {
        val db = mDbHelper.writableDatabase
        db.enableWriteAheadLogging()
        try {
            db.beginTransaction()
            db.delete("notification", null, null)
            for (noti in notiList) {
                val values = ContentValues()
                values.put("noti_id", noti.notiId)
                values.put("type", noti.type)
                values.put("title", noti.title)
                values.put("content", noti.content)
                values.put("send_date", noti.sendDate)
                values.put("status", noti.status)
                values.put("comment", noti.comment)
                db.insert("notification", null, values)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot parse notification object.")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /*** Get Notification By ID  */
    fun getNotiById(notiId: String): Notification? {
        val db = mDbHelper.readableDatabase
        val sql = "SELECT * FROM notification where noti_id = ?"
        val args = arrayOf(notiId)
        val mCur = db.rawQuery(sql, args)
        try {
            if (mCur.moveToFirst()) {
                val noti = Notification()
                noti.notiId = mCur.getString(mCur.getColumnIndex("noti_id"))
                noti.type = mCur.getString(mCur.getColumnIndex("type"))
                noti.title = mCur.getString(mCur.getColumnIndex("title"))
                noti.comment = mCur.getString(mCur.getColumnIndex("comment"))
                noti.content = mCur.getString(mCur.getColumnIndex("content"))
                noti.sendDate = mCur.getString(mCur.getColumnIndex("send_date"))
                noti.status = mCur.getInt(mCur.getColumnIndex("status"))
                return noti
            }
        } catch (e: Exception) {
            Log.i(TAG, "Cannot parse notification object.")
        } finally {
            mCur.close()
            db.close()
        }
        return null
    }

    /*** Set Notification Read  */
    fun setNotiRead(notiId: String) {
        val db = mDbHelper.writableDatabase
        try {
            db.beginTransaction()
            val values = ContentValues()
            values.put("status", 2)
            val args = arrayOf(notiId)
            db.update("notification", values, "noti_id = ?", args)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot parse notification object.")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    /*** Get Student Account from SQLite  */
    fun getAccountById(accountId: String): Account? {
        val db = mDbHelper.readableDatabase
        val selectionArgs = arrayOf(accountId)
        val sql = "SELECT * FROM account WHERE account_id = ?"
        val mCur = db.rawQuery(sql, selectionArgs)
        try {
            if (mCur.moveToFirst()) {
                val account = Account()
                account.accountId = mCur.getInt(mCur.getColumnIndex("account_id"))
                account.companyID = mCur.getInt(mCur.getColumnIndex("company_id"))
                account.roleID = mCur.getInt(mCur.getColumnIndex("role_id"))
                account.email = mCur.getString(mCur.getColumnIndex("email"))
                account.name = mCur.getString(mCur.getColumnIndex("username"))
                account.avatar = mCur.getString(mCur.getColumnIndex("avatar"))
                account.isEmailChecked = mCur.getInt(mCur.getColumnIndex("is_email_checked"))
                account.phoneCode = mCur.getString(mCur.getColumnIndex("ph_code"))
                account.phoneNumber = mCur.getString(mCur.getColumnIndex("ph_number"))
                account.isPhoneChecked = mCur.getInt(mCur.getColumnIndex("is_phone_checked"))
                account.age = mCur.getInt(mCur.getColumnIndex("age"))
                account.gender = mCur.getInt(mCur.getColumnIndex("gender"))
                account.status = mCur.getInt(mCur.getColumnIndex("status"))
                account.registerType = mCur.getInt(mCur.getColumnIndex("register_type"))
                account.credit = mCur.getDouble(mCur.getColumnIndex("credit"))
                account.country = mCur.getString(mCur.getColumnIndex("country"))
                account.address = mCur.getString(mCur.getColumnIndex("address"))
                account.timeZone = mCur.getString(mCur.getColumnIndex("time_zone"))
                account.timezoneOffset = mCur.getDouble(mCur.getColumnIndex("timezone_offset"))
                account.speakingLang = mCur.getString(mCur.getColumnIndex("lang"))
                return account
            }
        } finally {
            mCur.close()
            db.close()
        }
        return null
    }

    fun insertAccount(account: Account) {
        val db = mDbHelper.writableDatabase
        val values = ContentValues()
        values.put("account_id", account.accountId)
        values.put("company_id", account.companyID)
        values.put("role_id", account.roleID)
        values.put("username", account.name)
        values.put("avatar", account.avatar)
        values.put("email", account.email)
        values.put("is_email_checked", account.isEmailChecked)
        values.put("ph_code", account.phoneCode)
        values.put("ph_number", account.phoneNumber)
        values.put("is_phone_checked", account.isPhoneChecked)
        values.put("age", account.age)
        values.put("gender", account.gender)
        values.put("status", account.status)
        values.put("register_type", account.registerType)
        values.put("address", account.address)
        values.put("country", account.country)
        values.put("lang", account.speakingLang)
        values.put("credit", account.credit)
        values.put("time_zone", account.timeZone)
        values.put("timezone_offset", account.timezoneOffset)

        try {
            db.beginTransaction()
            db.delete("account", null, null)
            db.insert("account", null, values)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Cannot parse account object.")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    companion object {

        protected val TAG = DBAdapter::class.java!!.getSimpleName()
    }
}