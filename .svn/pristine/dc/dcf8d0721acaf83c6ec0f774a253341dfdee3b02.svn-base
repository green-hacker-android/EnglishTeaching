package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Booking implements Serializable {

    private String mBookingId;
    private int mBookingType;  // 1. Lesson Booking, 2.Package Booking
    private String mTutorId;
    private String mLessonId;
    private String mStartDate;
    private String mEndDate;
    private int mBookingStatus;
    private String mBoardId;

    public Booking() {
    }

    public Booking(JSONObject json) throws JSONException, ParseException {
        mBookingId = json.getString("class_id");
        mBoardId = json.getString("board_id");
        mStartDate = setBookedDateUTC(json.getString("start_date"));
        mEndDate = setBookedDateUTC(json.getString("end_date"));
        mBookingStatus = json.getInt("status");
        mLessonId = json.getString("lesson_id");
        mBookingType = json.getInt("class_type");
        mTutorId = json.getString("tutor_id");
    }

    public String getBookingId() {
        return mBookingId;
    }

    public void setBookingId(String bookingId) {
        mBookingId = bookingId;
    }

    public int getBookingType() {
        return mBookingType;
    }

    public void setBookingType(int bookingType) {
        mBookingType = bookingType;
    }

    public String getTutorId() {
        return mTutorId;
    }

    public void setTutorId(String tutorId) {
        mTutorId = tutorId;
    }

    public String getLessonId() {
        return mLessonId;
    }

    public void setLessonId(String lessonId) {
        mLessonId = lessonId;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public int getBookingStatus() {
        return mBookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        mBookingStatus = bookingStatus;
    }

    public String getBoardId() {
        return mBoardId;
    }

    public void setBoardId(String boardId) {
        mBoardId = boardId;
    }

    public String setBookedDateUTC(String bookedDateUTC) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = dateFormat.parse(bookedDateUTC);

        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(utcDate);
    }

    public static class Status {
        public static final int ACTIVE = 1;
        //public static final int CHANGE = 2;
        public static final int CANCEL = 3;
        public static final int FINISH = 4;
        public static final int MISS = 5;
    }

    public static class Type {
        public static final int LESSON = 1;
        public static final int PACKAGE = 2;
    }
}
