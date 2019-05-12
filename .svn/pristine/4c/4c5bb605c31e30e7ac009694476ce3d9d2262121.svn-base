package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
    private String mMessageId;
    private String notiHistoryID;
    private String mode;
    private String mSender;
    private String signalType;
    private String messageType;
    private String messageCategory;
    private String priorityLevel;
    private String mTo;
    private String mBody;
    private String sendDate;
    private String classroomID;
    private String status;
    private String errorInfo;
    private String accountName;
    private String callCenterID;

    public ChatMessage() {
    }

    public ChatMessage(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            mMessageId = json.getString("message_id");
            if (json.has("notification_history_id"))
                notiHistoryID = json.getString("notification_history_id");
            if (json.has("mode"))
                mode = json.getString("mode");
            mSender = json.getString("account_id");
            if (json.has("signal_type"))
                signalType = json.getString("signal_type");
            if (json.has("message_type"))
                messageType = json.getString("message_type");
            if (json.has("message_category"))
                messageCategory = json.getString("message_category");
            if (json.has("priority_level"))
                priorityLevel = json.getString("priority_level");
            mBody = json.getString("message_content");
            if (json.has("send_to"))
                mTo = json.getString("send_to");
            if (json.has("send_date"))
                sendDate = json.getString("send_date");
            if (json.has("classroom_id"))
                classroomID = json.getString("classroom_id");
            if (json.has("status"))
                status = json.getString("status");
            if (json.has("error_info"))
                errorInfo = json.getString("error_info");
            if (json.has("account_name"))
                accountName = json.getString("account_name");
            if (json.has("call_center_id"))
                callCenterID = json.getString("call_center_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCallCenterID() {
        return callCenterID;
    }

    public void setCallCenterID(String callCenterID) {
        this.callCenterID = callCenterID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(String messageCategory) {
        this.messageCategory = messageCategory;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(String classroomID) {
        this.classroomID = classroomID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        mMessageId = messageId;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public String getNotiHistoryID() {
        return notiHistoryID;
    }

    public void setNotiHistoryID(String notiHistoryID) {
        this.notiHistoryID = notiHistoryID;
    }
}
