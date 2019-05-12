package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.util.LGCUtil

class Notification {
    var notiId: String? = null
    var type: String? = null
    var comment: String? = null
    var title: String? = null
    var content: String? = null
    var sendDate: String? = null
    // 1. unread
    // 2. read
    var status: Int = 0

    constructor() {}

    @Throws(JSONException::class, ParseException::class)
    constructor(json: JSONObject) {
        notiId = json.getString("noti_id")
        type = json.getString("noti_type")
        title = json.getString("noti_title")
        content = json.getString("noti_body")
        comment = json.getString("noti_comment")
        status = json.getInt("noti_status")
        sendDate = LGCUtil.convertToLocale(json.getString("noti_date"), CommonConstant.DATE_TIME_FORMAT)
    }
}
