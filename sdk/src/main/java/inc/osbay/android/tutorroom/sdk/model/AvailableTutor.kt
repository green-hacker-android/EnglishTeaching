package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class AvailableTutor : Serializable {
    var tutorId: String? = null
    var name: String? = null
    var email: String? = null
    var beanAccountPreference: JSONObject? = null
    var beanAccountCredit: JSONObject? = null
    var beanAgentProfile: JSONObject? = null

    constructor() {}

    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        tutorId = json.getString("Id")
        name = json.getString("UserName")
        email = json.getString("Email")
        beanAccountPreference = json.getJSONObject("BeanAccountPreference")
        beanAccountCredit = json.getJSONObject("BeanAccountCredit")
        beanAgentProfile = json.getJSONObject("BeanAgentProfile")
    }

    fun getmTutorId(): String? {
        return tutorId
    }

    fun setmTutorId(mTutorId: String) {
        this.tutorId = mTutorId
    }

    fun getmName(): String? {
        return name
    }

    fun setmName(mName: String) {
        this.name = mName
    }
}


