package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Tutor : Serializable {
    var tutorId: String? = null
    var companyId: String? = null
    var roleId: String? = null
    var name: String? = null
    var avatar: String? = null
    var email: String? = null
    var isCheckedEmail: String? = null
    var isCheckedPhone: String? = null
    var age: String? = null
    var gender: String? = null
    var status: String? = null
    var registeredType: String? = null
    var address: String? = null
    var country: String? = null
    var speciality: String? = null
    var rate: String? = null
    var teachingExp: String? = null
    var creditWeight: String? = null
    var introVoice: String? = null
    var introVideo: String? = null
    var location: String? = null
    var introduction: String? = null
    var tutorType: String? = null

    constructor() {}

    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        tutorId = json.getString("account_id")
        companyId = json.getString("company_id")
        roleId = json.getString("role_id")
        name = json.getString("username")
        avatar = json.getString("avatar")
        email = json.getString("email")
        isCheckedEmail = json.getString("is_check_email")
        isCheckedPhone = json.getString("is_check_phone")
        age = json.getString("age")
        gender = json.getString("gendar")
        status = json.getString("status")
        registeredType = json.getString("register_type")
        address = json.getString("address")
        country = json.getString("country")
        speciality = json.getString("Speciality")
        rate = json.getString("rate")
        teachingExp = json.getString("exp")
        creditWeight = json.getString("credit_weight")
        introduction = json.getString("introduction")
        introVoice = json.getString("audio")
        introVideo = json.getString("video")
        location = json.getString("location")
        tutorType = json.getString("tutor_type")
    }
}

