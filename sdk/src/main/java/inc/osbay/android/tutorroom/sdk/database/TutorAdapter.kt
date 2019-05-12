package inc.osbay.android.tutorroom.sdk.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.ArrayList
import inc.osbay.android.tutorroom.sdk.model.Schedule
import inc.osbay.android.tutorroom.sdk.model.Tutor

class TutorAdapter(context: Context) {

    private val mDbHelper: DataBaseHelper

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
                        tutor.tutorId = mCur.getString(mCur.getColumnIndex("account_id"))
                        tutor.companyId = mCur.getString(mCur.getColumnIndex("company_id"))
                        tutor.roleId = mCur.getString(mCur.getColumnIndex("role_id"))
                        tutor.name = mCur.getString(mCur.getColumnIndex("name"))
                        tutor.rate = mCur.getString(mCur.getColumnIndex("rate"))
                        tutor.teachingExp = mCur.getString(mCur.getColumnIndex("experience"))
                        tutor.creditWeight = mCur.getString(mCur.getColumnIndex("credit_weight"))
                        tutor.avatar = mCur.getString(mCur.getColumnIndex("avatar"))
                        tutor.speciality = mCur.getString(mCur.getColumnIndex("speciality"))
                        tutor.introVoice = mCur.getString(mCur.getColumnIndex("intro_voice"))
                        tutor.location = mCur.getString(mCur.getColumnIndex("location"))
                        tutor.introduction = mCur.getString(mCur.getColumnIndex("intro_text"))
                        tutor.introVideo = mCur.getString(mCur.getColumnIndex("video"))
                        tutor.email = mCur.getString(mCur.getColumnIndex("email"))
                        tutor.isCheckedEmail = mCur.getString(mCur.getColumnIndex("is_check_email"))
                        tutor.isCheckedPhone = mCur.getString(mCur.getColumnIndex("is_check_phone"))
                        tutor.age = mCur.getString(mCur.getColumnIndex("age"))
                        tutor.gender = mCur.getString(mCur.getColumnIndex("gender"))
                        tutor.status = mCur.getString(mCur.getColumnIndex("status"))
                        tutor.registeredType = mCur.getString(mCur.getColumnIndex("register_type"))
                        tutor.address = mCur.getString(mCur.getColumnIndex("address"))
                        tutor.country = mCur.getString(mCur.getColumnIndex("country"))
                        tutor.tutorType = mCur.getString(mCur.getColumnIndex("tutor_type"))
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

    init {
        mDbHelper = DataBaseHelper(context)
    }

    fun insertTutors(tutors: List<Tutor>) {
        val db = mDbHelper.writableDatabase

        try {
            db.beginTransaction()

            db.delete("tutor", null, null)

            for (tutor in tutors) {
                val values = ContentValues()
                values.put("account_id", tutor.tutorId)
                values.put("company_id", tutor.companyId)
                values.put("role_id", tutor.roleId)
                values.put("name", tutor.name)
                values.put("avatar", tutor.avatar)
                values.put("email", tutor.email)
                values.put("is_check_email", tutor.isCheckedEmail)
                values.put("is_check_phone", tutor.isCheckedPhone)
                values.put("age", tutor.age)
                values.put("gender", tutor.gender)
                values.put("status", tutor.status)
                values.put("register_type", tutor.registeredType)
                values.put("address", tutor.address)
                values.put("country", tutor.country)
                values.put("Speciality", tutor.speciality)
                values.put("rate", tutor.rate)
                values.put("experience", tutor.teachingExp)
                values.put("credit_weight", tutor.creditWeight)
                values.put("intro_voice", tutor.introVoice)
                values.put("video", tutor.introVideo)
                values.put("intro_text", tutor.introduction)
                values.put("tutor_type", tutor.tutorType)
                values.put("location", tutor.location)

                db.insert("tutor", null, values)
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Tutor information update fail.", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getTutorById(tutorId: String): Tutor? {
        val db = mDbHelper.readableDatabase

        val selectionArgs = arrayOf(tutorId)

        try {
            val sql = "SELECT * FROM tutor WHERE account_id = ?"

            val mCur = db.rawQuery(sql, selectionArgs)
            if (mCur.moveToFirst()) {
                val tutor = Tutor()
                tutor.tutorId = mCur.getString(mCur.getColumnIndex("account_id"))
                tutor.companyId = mCur.getString(mCur.getColumnIndex("company_id"))
                tutor.roleId = mCur.getString(mCur.getColumnIndex("role_id"))
                tutor.name = mCur.getString(mCur.getColumnIndex("name"))
                tutor.rate = mCur.getString(mCur.getColumnIndex("rate"))
                tutor.teachingExp = mCur.getString(mCur.getColumnIndex("experience"))
                tutor.creditWeight = mCur.getString(mCur.getColumnIndex("credit_weight"))
                tutor.avatar = mCur.getString(mCur.getColumnIndex("avatar"))
                tutor.speciality = mCur.getString(mCur.getColumnIndex("speciality"))
                tutor.introVoice = mCur.getString(mCur.getColumnIndex("intro_voice"))
                tutor.location = mCur.getString(mCur.getColumnIndex("location"))
                tutor.introduction = mCur.getString(mCur.getColumnIndex("intro_text"))
                tutor.introVideo = mCur.getString(mCur.getColumnIndex("video"))
                tutor.email = mCur.getString(mCur.getColumnIndex("email"))
                tutor.isCheckedEmail = mCur.getString(mCur.getColumnIndex("is_check_email"))
                tutor.isCheckedPhone = mCur.getString(mCur.getColumnIndex("is_check_phone"))
                tutor.age = mCur.getString(mCur.getColumnIndex("age"))
                tutor.gender = mCur.getString(mCur.getColumnIndex("gender"))
                tutor.status = mCur.getString(mCur.getColumnIndex("status"))
                tutor.registeredType = mCur.getString(mCur.getColumnIndex("register_type"))
                tutor.address = mCur.getString(mCur.getColumnIndex("address"))
                tutor.country = mCur.getString(mCur.getColumnIndex("country"))
                tutor.tutorType = mCur.getString(mCur.getColumnIndex("tutor_type"))
                return tutor
            }

            mCur.close()
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
        } finally {
            db.close()
        }
        return null
    }

    fun searchTutorByName(tutorName: String): List<Tutor> {
        val tutors = ArrayList<Tutor>()
        val db = mDbHelper.readableDatabase
        try {
            val sql = "SELECT * FROM tutor WHERE name LIKE '$tutorName%'"

            val mCur = db.rawQuery(sql, null)
            if (mCur.moveToFirst()) {
                do {
                    val tutor = Tutor()
                    tutor.tutorId = mCur.getString(mCur.getColumnIndex("account_id"))
                    tutor.companyId = mCur.getString(mCur.getColumnIndex("company_id"))
                    tutor.roleId = mCur.getString(mCur.getColumnIndex("role_id"))
                    tutor.name = mCur.getString(mCur.getColumnIndex("name"))
                    tutor.rate = mCur.getString(mCur.getColumnIndex("rate"))
                    tutor.teachingExp = mCur.getString(mCur.getColumnIndex("experience"))
                    tutor.creditWeight = mCur.getString(mCur.getColumnIndex("credit_weight"))
                    tutor.avatar = mCur.getString(mCur.getColumnIndex("avatar"))
                    tutor.speciality = mCur.getString(mCur.getColumnIndex("speciality"))
                    tutor.introVoice = mCur.getString(mCur.getColumnIndex("intro_voice"))
                    tutor.location = mCur.getString(mCur.getColumnIndex("location"))
                    tutor.introduction = mCur.getString(mCur.getColumnIndex("intro_text"))
                    tutor.introVideo = mCur.getString(mCur.getColumnIndex("video"))
                    tutor.email = mCur.getString(mCur.getColumnIndex("email"))
                    tutor.isCheckedEmail = mCur.getString(mCur.getColumnIndex("is_check_email"))
                    tutor.isCheckedPhone = mCur.getString(mCur.getColumnIndex("is_check_phone"))
                    tutor.age = mCur.getString(mCur.getColumnIndex("age"))
                    tutor.gender = mCur.getString(mCur.getColumnIndex("gender"))
                    tutor.status = mCur.getString(mCur.getColumnIndex("status"))
                    tutor.registeredType = mCur.getString(mCur.getColumnIndex("register_type"))
                    tutor.address = mCur.getString(mCur.getColumnIndex("address"))
                    tutor.country = mCur.getString(mCur.getColumnIndex("country"))
                    tutor.tutorType = mCur.getString(mCur.getColumnIndex("tutor_type"))
                    tutors.add(tutor)
                } while (mCur.moveToNext())
            }
            mCur.close()
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return tutors
    }

    fun checkTutorAvailableForLesson(tutorId: String, firstBlock: String, secondBlock: String): Boolean {
        Log.e(TAG, tutorId + firstBlock + secondBlock)
        val db = mDbHelper.readableDatabase
        try {
            val selectionArgs = arrayOf(tutorId, firstBlock, secondBlock)
            val sql = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)"

            val numRows = DatabaseUtils.longForQuery(db, sql, selectionArgs).toInt()
            if (numRows == 2) {
                return true
            }
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return false
    }

    fun checkTutorAvailableForTopic(tutorId: String, firstBlock: String): Boolean {
        Log.e(TAG, tutorId + firstBlock)
        val db = mDbHelper.readableDatabase
        try {
            val selectionArgs = arrayOf(tutorId, firstBlock)
            val sql = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND start_time = ?"

            val numRows = DatabaseUtils.longForQuery(db, sql, selectionArgs).toInt()
            if (numRows > 0) {
                return true
            }
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return false
    }

    fun getAvailableTutorsForLesson(firstBlock: String, secondBlock: String): List<Tutor> {
        val tutors = ArrayList<Tutor>()

        val db = mDbHelper.readableDatabase
        try {

            val sql1 = "SELECT * FROM tutor"

            val mCur1 = db.rawQuery(sql1, null)
            if (mCur1.moveToFirst()) {
                do {
                    val tutor = Tutor()
                    tutor.tutorId = mCur1.getString(mCur1.getColumnIndex("account_id"))
                    tutor.companyId = mCur1.getString(mCur1.getColumnIndex("company_id"))
                    tutor.roleId = mCur1.getString(mCur1.getColumnIndex("role_id"))
                    tutor.name = mCur1.getString(mCur1.getColumnIndex("name"))
                    tutor.rate = mCur1.getString(mCur1.getColumnIndex("rate"))
                    tutor.teachingExp = mCur1.getString(mCur1.getColumnIndex("experience"))
                    tutor.creditWeight = mCur1.getString(mCur1.getColumnIndex("credit_weight"))
                    tutor.avatar = mCur1.getString(mCur1.getColumnIndex("avatar"))
                    tutor.speciality = mCur1.getString(mCur1.getColumnIndex("speciality"))
                    tutor.introVoice = mCur1.getString(mCur1.getColumnIndex("intro_voice"))
                    tutor.location = mCur1.getString(mCur1.getColumnIndex("location"))
                    tutor.introduction = mCur1.getString(mCur1.getColumnIndex("intro_text"))
                    tutor.introVideo = mCur1.getString(mCur1.getColumnIndex("video"))
                    tutor.email = mCur1.getString(mCur1.getColumnIndex("email"))
                    tutor.isCheckedEmail = mCur1.getString(mCur1.getColumnIndex("is_check_email"))
                    tutor.isCheckedPhone = mCur1.getString(mCur1.getColumnIndex("is_check_phone"))
                    tutor.age = mCur1.getString(mCur1.getColumnIndex("age"))
                    tutor.gender = mCur1.getString(mCur1.getColumnIndex("gender"))
                    tutor.status = mCur1.getString(mCur1.getColumnIndex("status"))
                    tutor.registeredType = mCur1.getString(mCur1.getColumnIndex("register_type"))
                    tutor.address = mCur1.getString(mCur1.getColumnIndex("address"))
                    tutor.country = mCur1.getString(mCur1.getColumnIndex("country"))
                    tutor.tutorType = mCur1.getString(mCur1.getColumnIndex("tutor_type"))
                    val sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)"

                    val selectionArgs = arrayOf(tutor.tutorId, firstBlock, secondBlock)
                    val numRows = DatabaseUtils.longForQuery(db, sql2, selectionArgs).toInt()
                    if (numRows == 2) {
                        tutors.add(tutor)
                    }
                } while (mCur1.moveToNext())
                mCur1.close()
                return tutors
            }
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return tutors
    }

    fun getAvailableTutorsForTopic(firstBlock: String): List<Tutor> {
        val tutors = ArrayList<Tutor>()

        val db = mDbHelper.readableDatabase
        try {

            val sql1 = "SELECT * FROM tutor"

            val mCur1 = db.rawQuery(sql1, null)
            if (mCur1.moveToFirst()) {
                do {
                    val tutor = Tutor()
                    tutor.tutorId = mCur1.getString(mCur1.getColumnIndex("account_id"))
                    tutor.companyId = mCur1.getString(mCur1.getColumnIndex("company_id"))
                    tutor.roleId = mCur1.getString(mCur1.getColumnIndex("role_id"))
                    tutor.name = mCur1.getString(mCur1.getColumnIndex("name"))
                    tutor.rate = mCur1.getString(mCur1.getColumnIndex("rate"))
                    tutor.teachingExp = mCur1.getString(mCur1.getColumnIndex("experience"))
                    tutor.creditWeight = mCur1.getString(mCur1.getColumnIndex("credit_weight"))
                    tutor.avatar = mCur1.getString(mCur1.getColumnIndex("avatar"))
                    tutor.speciality = mCur1.getString(mCur1.getColumnIndex("speciality"))
                    tutor.introVoice = mCur1.getString(mCur1.getColumnIndex("intro_voice"))
                    tutor.location = mCur1.getString(mCur1.getColumnIndex("location"))
                    tutor.introduction = mCur1.getString(mCur1.getColumnIndex("intro_text"))
                    tutor.introVideo = mCur1.getString(mCur1.getColumnIndex("video"))
                    tutor.email = mCur1.getString(mCur1.getColumnIndex("email"))
                    tutor.isCheckedEmail = mCur1.getString(mCur1.getColumnIndex("is_check_email"))
                    tutor.isCheckedPhone = mCur1.getString(mCur1.getColumnIndex("is_check_phone"))
                    tutor.age = mCur1.getString(mCur1.getColumnIndex("age"))
                    tutor.gender = mCur1.getString(mCur1.getColumnIndex("gender"))
                    tutor.status = mCur1.getString(mCur1.getColumnIndex("status"))
                    tutor.registeredType = mCur1.getString(mCur1.getColumnIndex("register_type"))
                    tutor.address = mCur1.getString(mCur1.getColumnIndex("address"))
                    tutor.country = mCur1.getString(mCur1.getColumnIndex("country"))
                    tutor.tutorType = mCur1.getString(mCur1.getColumnIndex("tutor_type"))
                    val sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND start_time = ?"

                    val selectionArgs = arrayOf(tutor.tutorId, firstBlock)
                    val numRows = DatabaseUtils.longForQuery(db, sql2, selectionArgs).toInt()
                    if (numRows > 0) {
                        tutors.add(tutor)
                    }
                } while (mCur1.moveToNext())
                mCur1.close()
                return tutors
            }
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return tutors
    }

    fun getAvailableTutorIdForSchedule(firstBlock: String, secondBlock: String): String? {

        val db = mDbHelper.readableDatabase
        try {

            val sql1 = "SELECT * FROM tutor"

            val mCur1 = db.rawQuery(sql1, null)
            if (mCur1.moveToFirst()) {
                do {
                    val tutorId = mCur1.getString(mCur1.getColumnIndex("tutor_id"))

                    val sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)"

                    val selectionArgs = arrayOf(tutorId, firstBlock, secondBlock)
                    val numRows = DatabaseUtils.longForQuery(db, sql2, selectionArgs).toInt()
                    if (numRows == 2) {
                        mCur1.close()
                        return tutorId
                    }
                } while (mCur1.moveToNext())
                mCur1.close()
            }
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>$mSQLException")
            throw mSQLException
        } finally {
            db.close()
        }
        return null
    }

    fun insertSchedules(schedules: List<Schedule>) {
        val db = mDbHelper.writableDatabase

        try {
            db.beginTransaction()

            db.delete("tutor_schedules", null, null)

            for (schedule in schedules) {
                val values = ContentValues()
                values.put("schedule_id", schedule.scheduleId)
                values.put("tutor_id", schedule.tutorId)
                values.put("start_time", schedule.startTime)
                values.put("end_time", schedule.endTime)

                db.insert("tutor_schedules", null, values)
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Schedule information update fail.", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun insertTutor(tutor: Tutor) {
        val db = mDbHelper.writableDatabase

        db.beginTransaction()

        try {
            val values = ContentValues()
            values.put("tutor_id", tutor.tutorId)
            values.put("name", tutor.name)
            values.put("rate", tutor.rate)
            values.put("experience", tutor.teachingExp)
            values.put("credit_weight", tutor.creditWeight)
            values.put("intro_voice", tutor.introVoice)
            values.put("avatar", tutor.avatar)
            values.put("location", tutor.location)

            db.insertWithOnConflict("tutor", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "Insert Failed.")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    companion object {
        protected val TAG = TutorAdapter::class.java!!.getSimpleName()
    }
}