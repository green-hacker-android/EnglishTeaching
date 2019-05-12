package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class Language @Throws(JSONException::class)
constructor(json: JSONObject) : Serializable {

    var languageID: String? = null
    var languageName: String? = null
    var shortName: String? = null

    init {
        languageID = json.getString("Id")
        languageName = json.getString("Name")
        shortName = json.getString("ShortName")
    }
}
