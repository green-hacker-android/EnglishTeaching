package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class Lesson : Serializable {
    var lessonId: String? = null
    var lessonPrice: Double = 0.toDouble()
    var lessonName: String? = null
    var lessonDescription: String? = null
    var lessonCover: String? = null
    var classMin: Int = 0
    var lessonPath: String? = null

    constructor() {}

    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        lessonId = json.getString("lesson_id")
        lessonPrice = json.getDouble("lesson_price")
        lessonName = json.getString("lesson_name")
        lessonDescription = json.getString("lesson_description")
        lessonCover = json.getString("lesson_cover")
        classMin = json.getInt("class_min")
        lessonPath = json.getString("lesson_path")
    }
}
