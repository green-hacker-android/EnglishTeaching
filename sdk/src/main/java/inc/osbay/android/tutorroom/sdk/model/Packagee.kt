package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Packagee @Throws(JSONException::class)
constructor(json: JSONObject) : Serializable {
    var packageID: String? = null
    var packagePrice: Double = 0.toDouble()
    var packageName: String? = null
    var packageDescription: String? = null
    var coverImg: String? = null
    var totalTime: Double = 0.toDouble()
    var lessonCount: Int = 0
    var lessonJsonArray: JSONArray? = null

    init {
        packageID = json.getString("package_id")
        packagePrice = java.lang.Double.parseDouble(json.getString("package_price"))
        packageName = json.getString("package_name")
        packageDescription = json.getString("package_description")
        coverImg = json.getString("cover")
        if (json.has("total_time"))
            totalTime = json.getDouble("total_time")
        if (json.has("lesson_count"))
            lessonCount = Integer.parseInt(json.getString("lesson_count"))
        if (json.has("lesson_list"))
            lessonJsonArray = JSONArray(json.getString("lesson_list"))
    }
}
