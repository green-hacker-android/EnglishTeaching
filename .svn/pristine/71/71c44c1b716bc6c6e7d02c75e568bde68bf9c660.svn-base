package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;

public class Notification {
    private String mNotiId;
    private String mType;
    private String comment;
    private String title;
    private String mContent;
    private String mSendDate;
    // 1. unread
    // 2. read
    private int mStatus;

    public Notification() {
    }

    public Notification(JSONObject json) throws JSONException, ParseException {
        mNotiId = json.getString("noti_id");
        mType = json.getString("noti_type");
        title = json.getString("noti_title");
        mContent = json.getString("noti_body");
        comment = json.getString("noti_comment");
        mStatus = json.getInt("noti_status");
        mSendDate = LGCUtil.convertToLocale(json.getString("noti_date"), CommonConstant.DATE_TIME_FORMAT);
    }

    public String getNotiId() {
        return mNotiId;
    }

    public void setNotiId(String notiId) {
        mNotiId = notiId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getSendDate() {
        return mSendDate;
    }

    public void setSendDate(String sendDate) {
        mSendDate = sendDate;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }
}
