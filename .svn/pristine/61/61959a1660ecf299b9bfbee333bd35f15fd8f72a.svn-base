package inc.osbay.android.tutorroom.sdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.sdk.model.Booking;
import inc.osbay.android.tutorroom.sdk.model.CountryCode;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.model.LessonDuration;
import inc.osbay.android.tutorroom.sdk.model.Notification;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;

public class DBAdapter {

    protected static final String TAG = DBAdapter.class.getSimpleName();

    private DataBaseHelper mDbHelper;

    public DBAdapter(Context context) {
        mDbHelper = new DataBaseHelper(context);
    }

    /*** Get Mobile Country Code ***/
    public List<CountryCode> getCountryCodes() {
        List<CountryCode> countryCodes = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT * FROM country_code WHERE phone_code > 0 ORDER BY nice_name";
        Cursor mCur = db.rawQuery(sql, null);
        try {
            if (mCur.moveToFirst()) {
                do {
                    CountryCode code = new CountryCode();

                    code.setCodeId(mCur.getInt(mCur.getColumnIndex("code_id")));
                    code.setCountry(mCur.getString(mCur.getColumnIndex("nice_name")));
                    code.setCode(mCur.getInt(mCur.getColumnIndex("phone_code")));

                    countryCodes.add(code);
                } while (mCur.moveToNext());

                return countryCodes;
            }
        } finally {
            mCur.close();
            db.close();
        }
        return countryCodes;
    }

    /*** Get Company ID ***/
    public String getCompanyID() {
        String companyID = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT * FROM company WHERE company_name='Engleezi'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                companyID = cursor.getString(cursor.getColumnIndex("id"));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return companyID;
    }

    /*** Insert Lessons ***/
    public void insertLessons(List<Lesson> newLessons) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            //db.delete("lesson", null, null);
            for (Lesson newLesson : newLessons) {
                int lessonCount = getLessonCountByID(newLesson.getLessonId());
                ContentValues values = new ContentValues();
                values.put("lesson_id", newLesson.getLessonId());
                values.put("lesson_name", newLesson.getLessonName());
                values.put("lesson_description", newLesson.getLessonDescription());
                values.put("lesson_price", newLesson.getLessonPrice());
                values.put("lesson_path", newLesson.getLessonPath());
                values.put("lesson_cover", newLesson.getLessonCover());
                values.put("class_min", newLesson.getClassMin());
                if (lessonCount == 0) {
                    db.insert("lesson", null, values);
                } else {
                    db.replace("lesson", null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Cannot insert lessons.", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Get Lesson List
     **/
    public List<Lesson> getLessonList() {
        List<Lesson> lessonList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT * FROM lesson";
        Cursor cursor = db.rawQuery(sql, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Lesson b = new Lesson();
                    b.setLessonId(cursor.getString(cursor.getColumnIndex("lesson_id")));
                    b.setLessonName(cursor.getString(cursor.getColumnIndex("lesson_name")));
                    b.setLessonDescription(cursor.getString(cursor.getColumnIndex("lesson_description")));
                    b.setLessonPrice(cursor.getDouble(cursor.getColumnIndex("lesson_price")));
                    b.setLessonPath(cursor.getString(cursor.getColumnIndex("lesson_path")));
                    b.setLessonCover(cursor.getString(cursor.getColumnIndex("lesson_cover")));
                    b.setClassMin(cursor.getInt(cursor.getColumnIndex("class_min")));
                    lessonList.add(b);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Bookings update fail.", e);
        } finally {
            cursor.close();
            db.close();
        }
        return lessonList;
    }

    /**
     * Get Lesson List
     **/
    public Lesson getLessonByID(String lessonID) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Lesson b = new Lesson();

        String[] selectionArgs = {lessonID};
        String sql = "SELECT * FROM lesson WHERE lesson_id = ?";
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        try {
            if (cursor.moveToFirst()) {
                do {
                    b.setLessonId(cursor.getString(cursor.getColumnIndex("lesson_id")));
                    b.setLessonName(cursor.getString(cursor.getColumnIndex("lesson_name")));
                    b.setLessonDescription(cursor.getString(cursor.getColumnIndex("lesson_description")));
                    b.setLessonPrice(cursor.getDouble(cursor.getColumnIndex("lesson_price")));
                    b.setLessonPath(cursor.getString(cursor.getColumnIndex("lesson_path")));
                    b.setLessonCover(cursor.getString(cursor.getColumnIndex("lesson_cover")));
                    b.setClassMin(cursor.getInt(cursor.getColumnIndex("class_min")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Bookings update fail.", e);
        } finally {
            cursor.close();
            db.close();
        }
        return b;
    }

    /*** Get Lesson Count (return integer value) ***/
    public int getLessonCountByID(String lessonID) {
        String countQuery = "SELECT * FROM lesson WHERE lesson_id = ?";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] args = {lessonID};
        Cursor cursor = db.rawQuery(countQuery, args);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    /*** Insert Booked Class ***/
    public void insertBookedClass(List<Booking> bookingList) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete("booking", null, null);
            for (Booking booking : bookingList) {
                ContentValues values = new ContentValues();
                values.put("class_id", booking.getBookingId());
                values.put("board_id", booking.getBoardId());
                values.put("start_date", booking.getStartDate());
                values.put("end_date", booking.getEndDate());
                values.put("status", booking.getBookingStatus());
                values.put("tutor_id", booking.getTutorId());
                values.put("lesson_id", booking.getLessonId());
                values.put("class_type", booking.getBookingType());
                db.insert("booking", null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Cannot insert bookings.", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /*** Get All Booked Class ***/
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT * FROM booking ORDER BY start_date ASC";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Booking b = new Booking();
                    b.setBookingId(cursor.getString(cursor.getColumnIndex("class_id")));
                    b.setBoardId(cursor.getString(cursor.getColumnIndex("board_id")));
                    b.setStartDate(cursor.getString(cursor.getColumnIndex("start_date")));
                    b.setEndDate(cursor.getString(cursor.getColumnIndex("end_date")));
                    b.setBookingStatus(cursor.getInt(cursor.getColumnIndex("status")));
                    b.setLessonId(cursor.getString(cursor.getColumnIndex("lesson_id")));
                    b.setBookingType(cursor.getInt(cursor.getColumnIndex("class_type")));
                    b.setTutorId(cursor.getString(cursor.getColumnIndex("tutor_id")));
                    bookings.add(b);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Bookings update fail.", e);
        } finally {
            cursor.close();
            db.close();
        }
        return bookings;
    }

    /*** Get Filtered Booked Class ***/
    public List<Booking> getFilteredBookings(String selectedDate) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT * FROM booking ORDER BY start_date ASC";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (selectedDate.equals(LGCUtil.convertToNoTime(cursor.getString(cursor.getColumnIndex("start_date"))))) {
                        Booking b = new Booking();
                        b.setBookingId(cursor.getString(cursor.getColumnIndex("class_id")));
                        b.setBoardId(cursor.getString(cursor.getColumnIndex("board_id")));
                        b.setStartDate(cursor.getString(cursor.getColumnIndex("start_date")));
                        b.setEndDate(cursor.getString(cursor.getColumnIndex("end_date")));
                        b.setBookingStatus(cursor.getInt(cursor.getColumnIndex("status")));
                        b.setLessonId(cursor.getString(cursor.getColumnIndex("lesson_id")));
                        b.setBookingType(cursor.getInt(cursor.getColumnIndex("class_type")));
                        b.setTutorId(cursor.getString(cursor.getColumnIndex("tutor_id")));
                        bookings.add(b);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Bookings update fail.", e);
        } finally {
            cursor.close();
            db.close();
        }
        return bookings;
    }

    /*** Delete All Tables ***/
    public void deleteAllTableData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.delete("account", null, null);
        //db.delete("badges", null, null);
        db.delete("booking", null, null);
        db.delete("country", null, null);
        db.delete("lesson", null, null);
       /* db.delete("dialog", null, null);
        db.delete("flashcard", null, null);
        db.delete("flashcard_deck", null, null);
        db.delete("lesson", null, null);
        db.delete("note", null, null);*/
        db.delete("notification", null, null);
        //db.delete("package", null, null);
        db.delete("tutor", null, null);
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

    /*** Insert Notifications ***/
    public void insertNotifications(List<Notification> notiList) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.enableWriteAheadLogging();
        try {
            db.beginTransaction();
            db.delete("notification", null, null);
            for (Notification noti : notiList) {
                ContentValues values = new ContentValues();
                values.put("noti_id", noti.getNotiId());
                values.put("type", noti.getType());
                values.put("title", noti.getTitle());
                values.put("content", noti.getContent());
                values.put("send_date", noti.getSendDate());
                values.put("status", noti.getStatus());
                values.put("comment", noti.getComment());
                db.insert("notification", null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Cannot parse notification object.");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Notification> getNotificationByDateDesc() {
        List<Notification> notiList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT * FROM notification ORDER BY send_date DESC";

        Cursor mCur = db.rawQuery(sql, null);
        try {
            if (mCur.moveToFirst()) {
                do {
                    Notification noti = new Notification();
                    noti.setNotiId(mCur.getString(mCur.getColumnIndex("noti_id")));
                    noti.setType(mCur.getString(mCur.getColumnIndex("type")));
                    noti.setTitle(mCur.getString(mCur.getColumnIndex("title")));
                    noti.setComment(mCur.getString(mCur.getColumnIndex("comment")));
                    noti.setContent(mCur.getString(mCur.getColumnIndex("content")));
                    noti.setSendDate(mCur.getString(mCur.getColumnIndex("send_date")));
                    noti.setStatus(mCur.getInt(mCur.getColumnIndex("status")));
                    notiList.add(noti);
                } while (mCur.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot parse notification object.");
        } finally {
            mCur.close();
            db.close();
        }
        return notiList;
    }

    /*** Get Notification By ID ***/
    public Notification getNotiById(String notiId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT * FROM notification where noti_id = ?";
        String[] args = {notiId};
        Cursor mCur = db.rawQuery(sql, args);
        try {
            if (mCur.moveToFirst()) {
                Notification noti = new Notification();
                noti.setNotiId(mCur.getString(mCur.getColumnIndex("noti_id")));
                noti.setType(mCur.getString(mCur.getColumnIndex("type")));
                noti.setTitle(mCur.getString(mCur.getColumnIndex("title")));
                noti.setComment(mCur.getString(mCur.getColumnIndex("comment")));
                noti.setContent(mCur.getString(mCur.getColumnIndex("content")));
                noti.setSendDate(mCur.getString(mCur.getColumnIndex("send_date")));
                noti.setStatus(mCur.getInt(mCur.getColumnIndex("status")));
                return noti;
            }
        } catch (Exception e) {
            Log.i(TAG, "Cannot parse notification object.");
        } finally {
            mCur.close();
            db.close();
        }
        return null;
    }

    /*** Set Notification Read ***/
    public void setNotiRead(String notiId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("status", 2);
            String[] args = {notiId};
            db.update("notification", values, "noti_id = ?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Cannot parse notification object.");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /*** Get Noti Count (return integer value) ***/
    public int getNotiCount() {
        String countQuery = "SELECT * FROM notification WHERE status = ?";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] args = {"1"};
        Cursor cursor = db.rawQuery(countQuery, args);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    /*** Get Tutor List ***/
    public List<Tutor> getAllTutor() {
        List<Tutor> tutors = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String sql = "SELECT * FROM tutor";

            Cursor mCur = db.rawQuery(sql, null);
            if (mCur.moveToFirst()) {
                do {
                    Tutor tutor = new Tutor();

                    tutor.setTutorId(mCur.getString(mCur.getColumnIndex("tutor_id")));
                    tutor.setName(mCur.getString(mCur.getColumnIndex("name")));
                    tutor.setRate(mCur.getString(mCur.getColumnIndex("rate")));
                    tutor.setTeachingExp(mCur.getString(mCur.getColumnIndex("experience")));
                    tutor.setCreditWeight(mCur.getString(mCur.getColumnIndex("credit_weight")));
                    tutor.setAvatar(mCur.getString(mCur.getColumnIndex("avatar")));
                    tutor.setIntroVoice(mCur.getString(mCur.getColumnIndex("intro_voice")));
                    tutor.setLocation(mCur.getString(mCur.getColumnIndex("location")));

                    tutors.add(tutor);
                } while (mCur.moveToNext());
            }
            mCur.close();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
        } finally {
            db.close();
        }
        return tutors;
    }

    /*** Get Student Account from SQLite ***/
    public Account getAccountById(String accountId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] selectionArgs = {
                accountId
        };
        String sql = "SELECT * FROM account WHERE account_id = ?";
        Cursor mCur = db.rawQuery(sql, selectionArgs);
        try {
            if (mCur.moveToFirst()) {
                Account account = new Account();
                account.setAccountId(mCur.getInt(mCur.getColumnIndex("account_id")));
                account.setCompanyID(mCur.getInt(mCur.getColumnIndex("company_id")));
                account.setRoleID(mCur.getInt(mCur.getColumnIndex("role_id")));
                account.setEmail(mCur.getString(mCur.getColumnIndex("email")));
                account.setName(mCur.getString(mCur.getColumnIndex("username")));
                account.setAvatar(mCur.getString(mCur.getColumnIndex("avatar")));
                account.setIsEmailChecked(mCur.getInt(mCur.getColumnIndex("is_email_checked")));
                account.setPhoneCode(mCur.getString(mCur.getColumnIndex("ph_code")));
                account.setPhoneNumber(mCur.getString(mCur.getColumnIndex("ph_number")));
                account.setIsPhoneChecked(mCur.getInt(mCur.getColumnIndex("is_phone_checked")));
                account.setAge(mCur.getInt(mCur.getColumnIndex("age")));
                account.setGender(mCur.getInt(mCur.getColumnIndex("gender")));
                account.setStatus(mCur.getInt(mCur.getColumnIndex("status")));
                account.setRegisterType(mCur.getInt(mCur.getColumnIndex("register_type")));
                account.setCredit(mCur.getDouble(mCur.getColumnIndex("credit")));
                account.setCountry(mCur.getString(mCur.getColumnIndex("country")));
                account.setAddress(mCur.getString(mCur.getColumnIndex("address")));
                account.setTimeZone(mCur.getString(mCur.getColumnIndex("time_zone")));
                account.setTimezoneOffset(mCur.getDouble(mCur.getColumnIndex("timezone_offset")));
                account.setSpeakingLang(mCur.getString(mCur.getColumnIndex("lang")));
                return account;
            }
        } finally {
            mCur.close();
            db.close();
        }
        return null;
    }

    public void insertAccount(Account account) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("account_id", account.getAccountId());
        values.put("company_id", account.getCompanyID());
        values.put("role_id", account.getRoleID());
        values.put("username", account.getName());
        values.put("avatar", account.getAvatar());
        values.put("email", account.getEmail());
        values.put("is_email_checked", account.getIsEmailChecked());
        values.put("ph_code", account.getPhoneCode());
        values.put("ph_number", account.getPhoneNumber());
        values.put("is_phone_checked", account.getIsPhoneChecked());
        values.put("age", account.getAge());
        values.put("gender", account.getGender());
        values.put("status", account.getStatus());
        values.put("register_type", account.getRegisterType());
        values.put("address", account.getAddress());
        values.put("country", account.getCountry());
        values.put("lang", account.getSpeakingLang());
        values.put("credit", account.getCredit());
        values.put("time_zone", account.getTimeZone());
        values.put("timezone_offset", account.getTimezoneOffset());

        try {
            db.beginTransaction();
            db.delete("account", null, null);
            db.insert("account", null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Cannot parse account object.");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<LessonDuration> getLessonDuration() {
        List<LessonDuration> durations = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT * FROM lesson_duration";
        Cursor mCur = db.rawQuery(sql, null);
        try {
            if (mCur.moveToFirst()) {
                do {
                    LessonDuration duration = new LessonDuration();

                    duration.setId(mCur.getString(mCur.getColumnIndex("lesson_id")));
                    duration.setStartHour(mCur.getInt(mCur.getColumnIndex("start_hour")));
                    duration.setStartMin(mCur.getInt(mCur.getColumnIndex("start_min")));
                    duration.setEndHour(mCur.getInt(mCur.getColumnIndex("end_hour")));
                    duration.setEndMin(mCur.getInt(mCur.getColumnIndex("end_min")));

                    durations.add(duration);
                } while (mCur.moveToNext());

                return durations;
            }
        } finally {
            mCur.close();
            db.close();
        }
        return durations;
    }
}