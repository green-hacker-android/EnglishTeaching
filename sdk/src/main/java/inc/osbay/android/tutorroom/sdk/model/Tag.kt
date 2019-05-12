package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Tag @Throws(JSONException::class)
constructor(json: JSONObject) : Serializable {
    var tagID: String? = null
    var tagType: String? = null
    var tagName: String? = null
    var tagDescription: String? = null
    var packageArray: JSONArray? = null
    var lessonArray: JSONArray? = null

    init {
        tagID = json.getString("tag_id")
        tagType = json.getString("tag_type")
        tagName = json.getString("tag_name")
        tagDescription = json.getString("tag_description")
        if (json.has("package_list"))
            packageArray = JSONArray(json.getString("package_list"))
        if (json.has("lesson_list"))
            lessonArray = JSONArray(json.getString("lesson_list"))
    }
}