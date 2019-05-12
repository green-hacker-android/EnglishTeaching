package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Account {
    private int mAccountId;
    private int companyID;
    private int roleID;
    private String mPhoneCode;
    private String mPhoneNumber;
    private String mEmail;
    private int mStatus;
    private int mGender;
    private String mAvatar;
    private String mTimeZone;
    private String address;
    private String mCountry;
    private int isEmailChecked;
    private int registerType;
    private double timezoneOffset;
    private int isPhoneChecked;
    private String mSpeakingLang;
    private int age;
    private String name;
    private double mCredit;

    public Account() {
    }

    public Account(JSONObject json) throws JSONException {
        mAccountId = Integer.parseInt(json.getString("account_id"));
        companyID = Integer.parseInt(json.getString("company_id"));
        roleID = Integer.parseInt(json.getString("role_id"));
        name = json.getString("username");
        mAvatar = json.getString("avatar");
        mEmail = json.getString("email");
        isEmailChecked = Integer.parseInt(json.getString("is_check_email"));
        mPhoneCode = json.getString("country_code");
        mPhoneNumber = json.getString("phone");
        isPhoneChecked = Integer.parseInt(json.getString("is_check_phone"));
        age = Integer.parseInt(json.getString("age"));
        mGender = Integer.parseInt(json.getString("gendar"));
        mStatus = Integer.parseInt(json.getString("status"));
        registerType = Integer.parseInt(json.getString("register_type"));
        address = json.getString("address");
        mCountry = json.getString("country");
        mSpeakingLang = json.getString("lang");
        mTimeZone = json.getString("time_zone");
        mCredit = json.getDouble("credit");
        timezoneOffset = Double.parseDouble(json.getString("time_zone_offset"));
    }

    public int getAccountId() {
        return mAccountId;
    }

    public void setAccountId(int accountId) {
        mAccountId = accountId;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIsEmailChecked() {
        return isEmailChecked;
    }

    public void setIsEmailChecked(int isEmailChecked) {
        this.isEmailChecked = isEmailChecked;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    public double getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(double timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public int getIsPhoneChecked() {
        return isPhoneChecked;
    }

    public void setIsPhoneChecked(int isPhoneChecked) {
        this.isPhoneChecked = isPhoneChecked;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneCode() {
        return mPhoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        mPhoneCode = phoneCode;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public double getCredit() {
        return mCredit;
    }

    public void setCredit(double credit) {
        mCredit = credit;
    }

    public String getSpeakingLang() {
        return mSpeakingLang;
    }

    public void setSpeakingLang(String speakingLang) {
        mSpeakingLang = speakingLang;
    }

    public static class Status {
        public static final int INACTIVE = 1;
        public static final int REQUEST = 2;
        public static final int TRIAL = 3;
        public static final int ACTIVE = 4;
        public static final int REQUEST_TRIAL = 5;
        public static final int NO_TRIAL_ACTIVE = 6;
        public static final int TRIAL_NOT_SHOW = 7;
    }
}
