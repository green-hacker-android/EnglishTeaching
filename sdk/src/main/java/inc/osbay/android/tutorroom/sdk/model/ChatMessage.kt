package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject

class ChatMessage {
    var messageId: String? = null
    var notiHistoryID: String? = null
    var mode: String? = null
    var sender: String? = null
    var signalType: String? = null
    var messageType: String? = null
    var messageCategory: String? = null
    var priorityLevel: String? = null
    var to: String? = null
    var body: String? = null
    var sendDate: String? = null
    var classroomID: String? = null
    var status: String? = null
    var errorInfo: String? = null
    var accountName: String? = null
    var callCenterID: String? = null

    constructor() {}

    constructor(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            messageId = json.getString("message_id")
            if (json.has("notification_history_id"))
                notiHistoryID = json.getString("notification_history_id")
            if (json.has("mode"))
                mode = json.getString("mode")
            sender = json.getString("account_id")
            if (json.has("signal_type"))
                signalType = json.getString("signal_type")
            if (json.has("message_type"))
                messageType = json.getString("message_type")
            if (json.has("message_category"))
                messageCategory = json.getString("message_category")
            if (json.has("priority_level"))
                priorityLevel = json.getString("priority_level")
            body = json.getString("message_content")
            if (json.has("send_to"))
                to = json.getString("send_to")
            if (json.has("send_date"))
                sendDate = json.getString("send_date")
            if (json.has("classroom_id"))
                classroomID = json.getString("classroom_id")
            if (json.has("status"))
                status = json.getString("status")
            if (json.has("error_info"))
                errorInfo = json.getString("error_info")
            if (json.has("account_name"))
                accountName = json.getString("account_name")
            if (json.has("call_center_id"))
                callCenterID = json.getString("call_center_id")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}
