package inc.osbay.android.tutorroom.sdk.client

import org.json.JSONException
import org.json.JSONObject

/**
 * Model class of Server Response.
 *
 * @author Ambrose
 */
class ServerResponse {

    var cmdSt: String? = null

    var dataSt: String? = null

    var code: Int = 0

    var message: String? = null

    var status: Int = 0

    val jsonObjResult: JSONObject?
        get() {
            try {
                return JSONObject(dataSt)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

    /**
     * Status Code of Server Response.
     *
     * @author Ambrose
     */
    object Status {

        /**
         * Status code for SUCCESS case.
         */
        val SUCCESS = 1
        val NO_DATA = 0
        val Account_NotExist = 1101
        val Account_PasswordError = 1102
        val Account_EmailAlreadyExit = 1103
        val Account_UserNameAlreadyExist = 1104
        val SendMail_Fail = 1105
        val Password_Set_Fail = 1106
        val Password_Set_Success = 1107
        val INSUFFICIENT_CREDIT = 1301


        /**
         * Status code for NEED REFRESH case to refresh token.
         */
        val NEED_REFRESH = 202

    }
}
