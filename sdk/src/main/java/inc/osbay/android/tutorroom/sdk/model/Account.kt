package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

class Account {
    var accountId: Int = 0
    var companyID: Int = 0
    var roleID: Int = 0
    var phoneCode: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var status: Int = 0
    var gender: Int = 0
    var avatar: String? = null
    var timeZone: String? = null
    var address: String? = null
    var country: String? = null
    var isEmailChecked: Int = 0
    var registerType: Int = 0
    var timezoneOffset: Double = 0.toDouble()
    var isPhoneChecked: Int = 0
    var speakingLang: String? = null
    var age: Int = 0
    var name: String? = null
    var credit: Double = 0.toDouble()

    constructor() {}

    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        accountId = Integer.parseInt(json.getString("account_id"))
        companyID = Integer.parseInt(json.getString("company_id"))
        roleID = Integer.parseInt(json.getString("role_id"))
        name = json.getString("username")
        avatar = json.getString("avatar")
        email = json.getString("email")
        isEmailChecked = Integer.parseInt(json.getString("is_check_email"))
        phoneCode = json.getString("country_code")
        phoneNumber = json.getString("phone")
        isPhoneChecked = Integer.parseInt(json.getString("is_check_phone"))
        age = Integer.parseInt(json.getString("age"))
        gender = Integer.parseInt(json.getString("gendar"))
        status = Integer.parseInt(json.getString("status"))
        registerType = Integer.parseInt(json.getString("register_type"))
        address = json.getString("address")
        country = json.getString("country")
        speakingLang = json.getString("lang")
        timeZone = json.getString("time_zone")
        credit = json.getDouble("credit")
        timezoneOffset = java.lang.Double.parseDouble(json.getString("time_zone_offset"))
    }

    object Status {
        val INACTIVE = 1
        val REQUEST = 2
        val TRIAL = 3
        val ACTIVE = 4
        val REQUEST_TRIAL = 5
        val NO_TRIAL_ACTIVE = 6
        val TRIAL_NOT_SHOW = 7
    }
}
