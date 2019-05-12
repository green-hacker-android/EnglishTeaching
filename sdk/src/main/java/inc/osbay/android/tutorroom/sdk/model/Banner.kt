package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

class Banner @Throws(JSONException::class)
constructor(json: JSONObject) {
    var bannerId: String? = null
    var bannerType: String? = null  // 1. Lesson Booking, 2.Package Booking
    var companyId: String? = null
    var position: String? = null
    var name: String? = null
    var image: String? = null
    var comments: String? = null
    var updatedate: String? = null

    init {
        bannerId = json.getString("id")
        companyId = json.getString("company_id")
        bannerType = json.getString("banner_type")
        position = json.getString("position")
        name = json.getString("name")
        image = json.getString("image")
        comments = json.getString("comments")
        updatedate = json.getString("updatedate")
    }
}
