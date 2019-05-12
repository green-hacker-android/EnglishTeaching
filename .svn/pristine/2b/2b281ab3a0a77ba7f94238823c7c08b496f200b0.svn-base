package inc.osbay.android.tutorroom.sdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import inc.osbay.android.tutorroom.sdk.model.Schedule;
import inc.osbay.android.tutorroom.sdk.model.Tutor;

public class TutorAdapter {
    protected static final String TAG = TutorAdapter.class.getSimpleName();

    private DataBaseHelper mDbHelper;

    public TutorAdapter(Context context) {
        mDbHelper = new DataBaseHelper(context);
    }

    public void insertTutors(List<Tutor> tutors) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete("tutor", null, null);

            for (Tutor tutor : tutors) {
                ContentValues values = new ContentValues();
                values.put("account_id", tutor.getTutorId());
                values.put("company_id", tutor.getCompanyId());
                values.put("role_id", tutor.getRoleId());
                values.put("name", tutor.getName());
                values.put("avatar", tutor.getAvatar());
                values.put("email", tutor.getEmail());
                values.put("is_check_email", tutor.getIsCheckedEmail());
                values.put("is_check_phone", tutor.getIsCheckedPhone());
                values.put("age", tutor.getAge());
                values.put("gender", tutor.getGender());
                values.put("status", tutor.getStatus());
                values.put("register_type", tutor.getRegisteredType());
                values.put("address", tutor.getAddress());
                values.put("country", tutor.getCountry());
                values.put("Speciality", tutor.getSpeciality());
                values.put("rate", tutor.getRate());
                values.put("experience", tutor.getTeachingExp());
                values.put("credit_weight", tutor.getCreditWeight());
                values.put("intro_voice", tutor.getIntroVoice());
                values.put("video", tutor.getIntroVideo());
                values.put("intro_text", tutor.getIntroduction());
                values.put("tutor_type", tutor.getTutorType());
                values.put("location", tutor.getLocation());

                db.insert("tutor", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Tutor information update fail.", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Tutor> getAllTutor() {
        List<Tutor> tutors = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String sql = "SELECT * FROM tutor";

            Cursor mCur = db.rawQuery(sql, null);
            if (mCur.moveToFirst()) {
                do {
                    Tutor tutor = new Tutor();
                    tutor.setTutorId(mCur.getString(mCur.getColumnIndex("account_id")));
                    tutor.setCompanyId(mCur.getString(mCur.getColumnIndex("company_id")));
                    tutor.setRoleId(mCur.getString(mCur.getColumnIndex("role_id")));
                    tutor.setName(mCur.getString(mCur.getColumnIndex("name")));
                    tutor.setRate(mCur.getString(mCur.getColumnIndex("rate")));
                    tutor.setTeachingExp(mCur.getString(mCur.getColumnIndex("experience")));
                    tutor.setCreditWeight(mCur.getString(mCur.getColumnIndex("credit_weight")));
                    tutor.setAvatar(mCur.getString(mCur.getColumnIndex("avatar")));
                    tutor.setSpeciality(mCur.getString(mCur.getColumnIndex("speciality")));
                    tutor.setIntroVoice(mCur.getString(mCur.getColumnIndex("intro_voice")));
                    tutor.setLocation(mCur.getString(mCur.getColumnIndex("location")));
                    tutor.setIntroduction(mCur.getString(mCur.getColumnIndex("intro_text")));
                    tutor.setIntroVideo(mCur.getString(mCur.getColumnIndex("video")));
                    tutor.setEmail(mCur.getString(mCur.getColumnIndex("email")));
                    tutor.setIsCheckedEmail(mCur.getString(mCur.getColumnIndex("is_check_email")));
                    tutor.setIsCheckedPhone(mCur.getString(mCur.getColumnIndex("is_check_phone")));
                    tutor.setAge(mCur.getString(mCur.getColumnIndex("age")));
                    tutor.setGender(mCur.getString(mCur.getColumnIndex("gender")));
                    tutor.setStatus(mCur.getString(mCur.getColumnIndex("status")));
                    tutor.setRegisteredType(mCur.getString(mCur.getColumnIndex("register_type")));
                    tutor.setAddress(mCur.getString(mCur.getColumnIndex("address")));
                    tutor.setCountry(mCur.getString(mCur.getColumnIndex("country")));
                    tutor.setTutorType(mCur.getString(mCur.getColumnIndex("tutor_type")));
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

    public Tutor getTutorById(String tutorId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] selectionArgs = {tutorId};

        try {
            String sql = "SELECT * FROM tutor WHERE account_id = ?";

            Cursor mCur = db.rawQuery(sql, selectionArgs);
            if (mCur.moveToFirst()) {
                Tutor tutor = new Tutor();
                tutor.setTutorId(mCur.getString(mCur.getColumnIndex("account_id")));
                tutor.setCompanyId(mCur.getString(mCur.getColumnIndex("company_id")));
                tutor.setRoleId(mCur.getString(mCur.getColumnIndex("role_id")));
                tutor.setName(mCur.getString(mCur.getColumnIndex("name")));
                tutor.setRate(mCur.getString(mCur.getColumnIndex("rate")));
                tutor.setTeachingExp(mCur.getString(mCur.getColumnIndex("experience")));
                tutor.setCreditWeight(mCur.getString(mCur.getColumnIndex("credit_weight")));
                tutor.setAvatar(mCur.getString(mCur.getColumnIndex("avatar")));
                tutor.setSpeciality(mCur.getString(mCur.getColumnIndex("speciality")));
                tutor.setIntroVoice(mCur.getString(mCur.getColumnIndex("intro_voice")));
                tutor.setLocation(mCur.getString(mCur.getColumnIndex("location")));
                tutor.setIntroduction(mCur.getString(mCur.getColumnIndex("intro_text")));
                tutor.setIntroVideo(mCur.getString(mCur.getColumnIndex("video")));
                tutor.setEmail(mCur.getString(mCur.getColumnIndex("email")));
                tutor.setIsCheckedEmail(mCur.getString(mCur.getColumnIndex("is_check_email")));
                tutor.setIsCheckedPhone(mCur.getString(mCur.getColumnIndex("is_check_phone")));
                tutor.setAge(mCur.getString(mCur.getColumnIndex("age")));
                tutor.setGender(mCur.getString(mCur.getColumnIndex("gender")));
                tutor.setStatus(mCur.getString(mCur.getColumnIndex("status")));
                tutor.setRegisteredType(mCur.getString(mCur.getColumnIndex("register_type")));
                tutor.setAddress(mCur.getString(mCur.getColumnIndex("address")));
                tutor.setCountry(mCur.getString(mCur.getColumnIndex("country")));
                tutor.setTutorType(mCur.getString(mCur.getColumnIndex("tutor_type")));
                return tutor;
            }

            mCur.close();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
        } finally {
            db.close();
        }
        return null;
    }

    public List<Tutor> searchTutorByName(String tutorName) {
        List<Tutor> tutors = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String sql = "SELECT * FROM tutor WHERE name LIKE '" + tutorName + "%'";

            Cursor mCur = db.rawQuery(sql, null);
            if (mCur.moveToFirst()) {
                do {
                    Tutor tutor = new Tutor();
                    tutor.setTutorId(mCur.getString(mCur.getColumnIndex("account_id")));
                    tutor.setCompanyId(mCur.getString(mCur.getColumnIndex("company_id")));
                    tutor.setRoleId(mCur.getString(mCur.getColumnIndex("role_id")));
                    tutor.setName(mCur.getString(mCur.getColumnIndex("name")));
                    tutor.setRate(mCur.getString(mCur.getColumnIndex("rate")));
                    tutor.setTeachingExp(mCur.getString(mCur.getColumnIndex("experience")));
                    tutor.setCreditWeight(mCur.getString(mCur.getColumnIndex("credit_weight")));
                    tutor.setAvatar(mCur.getString(mCur.getColumnIndex("avatar")));
                    tutor.setSpeciality(mCur.getString(mCur.getColumnIndex("speciality")));
                    tutor.setIntroVoice(mCur.getString(mCur.getColumnIndex("intro_voice")));
                    tutor.setLocation(mCur.getString(mCur.getColumnIndex("location")));
                    tutor.setIntroduction(mCur.getString(mCur.getColumnIndex("intro_text")));
                    tutor.setIntroVideo(mCur.getString(mCur.getColumnIndex("video")));
                    tutor.setEmail(mCur.getString(mCur.getColumnIndex("email")));
                    tutor.setIsCheckedEmail(mCur.getString(mCur.getColumnIndex("is_check_email")));
                    tutor.setIsCheckedPhone(mCur.getString(mCur.getColumnIndex("is_check_phone")));
                    tutor.setAge(mCur.getString(mCur.getColumnIndex("age")));
                    tutor.setGender(mCur.getString(mCur.getColumnIndex("gender")));
                    tutor.setStatus(mCur.getString(mCur.getColumnIndex("status")));
                    tutor.setRegisteredType(mCur.getString(mCur.getColumnIndex("register_type")));
                    tutor.setAddress(mCur.getString(mCur.getColumnIndex("address")));
                    tutor.setCountry(mCur.getString(mCur.getColumnIndex("country")));
                    tutor.setTutorType(mCur.getString(mCur.getColumnIndex("tutor_type")));
                    tutors.add(tutor);
                } while (mCur.moveToNext());
            }
            mCur.close();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return tutors;
    }

    public boolean checkTutorAvailableForLesson(String tutorId, String firstBlock, String secondBlock) {
        Log.e(TAG, tutorId + firstBlock + secondBlock);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String[] selectionArgs = {tutorId, firstBlock, secondBlock};
            String sql = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)";

            int numRows = (int) DatabaseUtils.longForQuery(db, sql, selectionArgs);
            if (numRows == 2) {
                return true;
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return false;
    }

    public boolean checkTutorAvailableForTopic(String tutorId, String firstBlock) {
        Log.e(TAG, tutorId + firstBlock);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String[] selectionArgs = {tutorId, firstBlock};
            String sql = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND start_time = ?";

            int numRows = (int) DatabaseUtils.longForQuery(db, sql, selectionArgs);
            if (numRows > 0) {
                return true;
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return false;
    }

    public List<Tutor> getAvailableTutorsForLesson(String firstBlock, String secondBlock) {
        List<Tutor> tutors = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            String sql1 = "SELECT * FROM tutor";

            Cursor mCur1 = db.rawQuery(sql1, null);
            if (mCur1.moveToFirst()) {
                do {
                    Tutor tutor = new Tutor();
                    tutor.setTutorId(mCur1.getString(mCur1.getColumnIndex("account_id")));
                    tutor.setCompanyId(mCur1.getString(mCur1.getColumnIndex("company_id")));
                    tutor.setRoleId(mCur1.getString(mCur1.getColumnIndex("role_id")));
                    tutor.setName(mCur1.getString(mCur1.getColumnIndex("name")));
                    tutor.setRate(mCur1.getString(mCur1.getColumnIndex("rate")));
                    tutor.setTeachingExp(mCur1.getString(mCur1.getColumnIndex("experience")));
                    tutor.setCreditWeight(mCur1.getString(mCur1.getColumnIndex("credit_weight")));
                    tutor.setAvatar(mCur1.getString(mCur1.getColumnIndex("avatar")));
                    tutor.setSpeciality(mCur1.getString(mCur1.getColumnIndex("speciality")));
                    tutor.setIntroVoice(mCur1.getString(mCur1.getColumnIndex("intro_voice")));
                    tutor.setLocation(mCur1.getString(mCur1.getColumnIndex("location")));
                    tutor.setIntroduction(mCur1.getString(mCur1.getColumnIndex("intro_text")));
                    tutor.setIntroVideo(mCur1.getString(mCur1.getColumnIndex("video")));
                    tutor.setEmail(mCur1.getString(mCur1.getColumnIndex("email")));
                    tutor.setIsCheckedEmail(mCur1.getString(mCur1.getColumnIndex("is_check_email")));
                    tutor.setIsCheckedPhone(mCur1.getString(mCur1.getColumnIndex("is_check_phone")));
                    tutor.setAge(mCur1.getString(mCur1.getColumnIndex("age")));
                    tutor.setGender(mCur1.getString(mCur1.getColumnIndex("gender")));
                    tutor.setStatus(mCur1.getString(mCur1.getColumnIndex("status")));
                    tutor.setRegisteredType(mCur1.getString(mCur1.getColumnIndex("register_type")));
                    tutor.setAddress(mCur1.getString(mCur1.getColumnIndex("address")));
                    tutor.setCountry(mCur1.getString(mCur1.getColumnIndex("country")));
                    tutor.setTutorType(mCur1.getString(mCur1.getColumnIndex("tutor_type")));
                    String sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)";

                    String[] selectionArgs = {tutor.getTutorId(), firstBlock, secondBlock};
                    int numRows = (int) DatabaseUtils.longForQuery(db, sql2, selectionArgs);
                    if (numRows == 2) {
                        tutors.add(tutor);
                    }
                } while (mCur1.moveToNext());
                mCur1.close();
                return tutors;
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return tutors;
    }

    public List<Tutor> getAvailableTutorsForTopic(String firstBlock) {
        List<Tutor> tutors = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            String sql1 = "SELECT * FROM tutor";

            Cursor mCur1 = db.rawQuery(sql1, null);
            if (mCur1.moveToFirst()) {
                do {
                    Tutor tutor = new Tutor();
                    tutor.setTutorId(mCur1.getString(mCur1.getColumnIndex("account_id")));
                    tutor.setCompanyId(mCur1.getString(mCur1.getColumnIndex("company_id")));
                    tutor.setRoleId(mCur1.getString(mCur1.getColumnIndex("role_id")));
                    tutor.setName(mCur1.getString(mCur1.getColumnIndex("name")));
                    tutor.setRate(mCur1.getString(mCur1.getColumnIndex("rate")));
                    tutor.setTeachingExp(mCur1.getString(mCur1.getColumnIndex("experience")));
                    tutor.setCreditWeight(mCur1.getString(mCur1.getColumnIndex("credit_weight")));
                    tutor.setAvatar(mCur1.getString(mCur1.getColumnIndex("avatar")));
                    tutor.setSpeciality(mCur1.getString(mCur1.getColumnIndex("speciality")));
                    tutor.setIntroVoice(mCur1.getString(mCur1.getColumnIndex("intro_voice")));
                    tutor.setLocation(mCur1.getString(mCur1.getColumnIndex("location")));
                    tutor.setIntroduction(mCur1.getString(mCur1.getColumnIndex("intro_text")));
                    tutor.setIntroVideo(mCur1.getString(mCur1.getColumnIndex("video")));
                    tutor.setEmail(mCur1.getString(mCur1.getColumnIndex("email")));
                    tutor.setIsCheckedEmail(mCur1.getString(mCur1.getColumnIndex("is_check_email")));
                    tutor.setIsCheckedPhone(mCur1.getString(mCur1.getColumnIndex("is_check_phone")));
                    tutor.setAge(mCur1.getString(mCur1.getColumnIndex("age")));
                    tutor.setGender(mCur1.getString(mCur1.getColumnIndex("gender")));
                    tutor.setStatus(mCur1.getString(mCur1.getColumnIndex("status")));
                    tutor.setRegisteredType(mCur1.getString(mCur1.getColumnIndex("register_type")));
                    tutor.setAddress(mCur1.getString(mCur1.getColumnIndex("address")));
                    tutor.setCountry(mCur1.getString(mCur1.getColumnIndex("country")));
                    tutor.setTutorType(mCur1.getString(mCur1.getColumnIndex("tutor_type")));
                    String sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND start_time = ?";

                    String[] selectionArgs = {tutor.getTutorId(), firstBlock};
                    int numRows = (int) DatabaseUtils.longForQuery(db, sql2, selectionArgs);
                    if (numRows > 0) {
                        tutors.add(tutor);
                    }
                } while (mCur1.moveToNext());
                mCur1.close();
                return tutors;
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return tutors;
    }

    public String getAvailableTutorIdForSchedule(String firstBlock, String secondBlock) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            String sql1 = "SELECT * FROM tutor";

            Cursor mCur1 = db.rawQuery(sql1, null);
            if (mCur1.moveToFirst()) {
                do {
                    String tutorId = mCur1.getString(mCur1.getColumnIndex("tutor_id"));

                    String sql2 = "SELECT COUNT(*) FROM tutor_schedules WHERE tutor_id = ? AND (start_time = ? OR start_time = ?)";

                    String[] selectionArgs = {tutorId, firstBlock, secondBlock};
                    int numRows = (int) DatabaseUtils.longForQuery(db, sql2, selectionArgs);
                    if (numRows == 2) {
                        mCur1.close();
                        return tutorId;
                    }
                } while (mCur1.moveToNext());
                mCur1.close();
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            db.close();
        }
        return null;
    }

    public void insertSchedules(List<Schedule> schedules) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete("tutor_schedules", null, null);

            for (Schedule schedule : schedules) {
                ContentValues values = new ContentValues();
                values.put("schedule_id", schedule.getScheduleId());
                values.put("tutor_id", schedule.getTutorId());
                values.put("start_time", schedule.getStartTime());
                values.put("end_time", schedule.getEndTime());

                db.insert("tutor_schedules", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Schedule information update fail.", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public void insertTutor(Tutor tutor) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("tutor_id", tutor.getTutorId());
            values.put("name", tutor.getName());
            values.put("rate", tutor.getRate());
            values.put("experience", tutor.getTeachingExp());
            values.put("credit_weight", tutor.getCreditWeight());
            values.put("intro_voice", tutor.getIntroVoice());
            values.put("avatar", tutor.getAvatar());
            values.put("location", tutor.getLocation());

            db.insertWithOnConflict("tutor", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Insert Failed.");
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}