package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class AvailableTutor implements Serializable {
    private String mTutorId;
    private String mName;
    private String email;
    private JSONObject beanAccountPreference;
    private JSONObject beanAccountCredit;
    private JSONObject beanAgentProfile;

    public AvailableTutor() {
    }

    public AvailableTutor(JSONObject json) throws JSONException {
        mTutorId = json.getString("Id");
        mName = json.getString("UserName");
        email = json.getString("Email");
        beanAccountPreference = json.getJSONObject("BeanAccountPreference");
        beanAccountCredit = json.getJSONObject("BeanAccountCredit");
        beanAgentProfile = json.getJSONObject("BeanAgentProfile");
    }

    public String getTutorId() {
        return mTutorId;
    }

    public void setTutorId(String tutorId) {
        mTutorId = tutorId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getmTutorId() {
        return mTutorId;
    }

    public void setmTutorId(String mTutorId) {
        this.mTutorId = mTutorId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public JSONObject getBeanAccountPreference() {
        return beanAccountPreference;
    }

    public void setBeanAccountPreference(JSONObject beanAccountPreference) {
        this.beanAccountPreference = beanAccountPreference;
    }

    public JSONObject getBeanAccountCredit() {
        return beanAccountCredit;
    }

    public void setBeanAccountCredit(JSONObject beanAccountCredit) {
        this.beanAccountCredit = beanAccountCredit;
    }

    public JSONObject getBeanAgentProfile() {
        return beanAgentProfile;
    }

    public void setBeanAgentProfile(JSONObject beanAgentProfile) {
        this.beanAgentProfile = beanAgentProfile;
    }
}


