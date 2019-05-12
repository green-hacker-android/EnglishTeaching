package inc.osbay.android.tutorroom.sdk.client

import com.android.volley.VolleyError

import org.json.JSONException
import org.json.JSONObject

/**
 * Model class of Server Error Object.
 *
 * @author Ambrose
 */
class ServerError : VolleyError {

    /**
     * Getter method of error code.
     *
     * @return Error code.
     */
    var errorCode: Int = 0

    private var mErrorMessage: String? = null

    /**
     * No argument constructor.
     */
    constructor() : super() {}

    /**
     * Constructor class for Server Error object.
     *
     * @param code Error code.
     * @param msg  Error message.
     */
    constructor(code: Int, msg: String) : super() {
        errorCode = code
        mErrorMessage = msg
    }

    /**
     * Constructor class for Server Error object. Parse json object to Server
     * Error object.
     *
     * @param json Error object with json format.
     * @throws JSONException If key name is not exists in json object, will
     * throw.
     */
    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        errorCode = json.getInt("err_code")
        mErrorMessage = json.getString("err_msg")
    }

    override fun getMessage(): String? {
        return mErrorMessage
    }

    fun setMessage(errorMessage: String) {
        mErrorMessage = errorMessage
    }

    override fun toString(): String {
        return ("ServerError (code=" + errorCode + ", msg=" + mErrorMessage
                + ")")
    }

    /**
     * Server Error Codes.
     *
     * @author Ambrose
     */
    object Code {

        /**
         * Client request token is expired.
         */
        val TOKEN_EXPIRED = 10029

        val JSON_EXCEPTION = 1

        val NETWORK_ERROR = 2

        val NO_CURRENT_PLAN = 10066

        val LESSON_BOOKED_IN_PACKAGE = 10086

        val CLASS_ALREADY_BOOKED = 10086

        val BOOKING_DATE_NOT_AVAILABLE = 10087

        val TOPIC_ALREADY_BOOKED = 10090

        val HIGHER_TOPIC_LEVEL = 10091

        val HIGHER_PACKAGE_LEVEL = 10076

        val LESSON_CONTAINS_IN_PACKAGE = 10097

        val INSUFFICIENT_BALANCE = 10052

        val CHINESE_LEVEL_LOWER = 10076

        val NO_TUTOR_SCHEDULE = 10065

        val NO_OPERATOR_ONLINE = 10077

    }
    /**
     * Email address is already exist. Cannot use this email to register new
     * account.
     */// public static final int EMAIL_ALREADY_EXISTS = 10005;
}
