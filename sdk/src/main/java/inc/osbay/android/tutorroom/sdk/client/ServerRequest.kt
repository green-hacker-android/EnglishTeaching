package inc.osbay.android.tutorroom.sdk.client

import android.content.Context
import android.util.Log

import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser

import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

import inc.osbay.android.tutorroom.sdk.R
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.util.LGCUtil

import inc.osbay.android.tutorroom.sdk.util.LGCUtil.md5

class ServerRequest
/**
 * Constructor with json object parameter.
 *
 * @param requestObj       Request object to parse to server.
 * @param responseListener Server response listener.
 * @param errorListener    Request error listener.
 */
(private val mContext: Context, command: String, requestObj: JSONObject,
 private val mListener: Response.Listener<ServerResponse>?,
 errorListener: Response.ErrorListener) : Request<ServerResponse>(Request.Method.POST, CommonConstant.WEB_SERVICE_URL, errorListener) {
    private val mParams: MutableMap<String, String>

    init {
        val queryObject = JSONObject()
        try {
            val mLocale = Locale.getDefault().language
            val encodedString = LGCUtil.base64(requestObj.toString())
            // 2. Buildup the sign string
            val DBAdapter = DBAdapter(mContext)
            val companyID = DBAdapter.companyID
            val requestDate = LGCUtil.currentUTCTimeString
            var signSimple = companyID + requestDate + command + encodedString
            signSimple = signSimple.trim { it <= ' ' }
            val signedString = md5(signSimple)

            val timezoneOffsetSt = (Calendar.getInstance().timeZone.rawOffset.toDouble() / 3600000).toString()
            queryObject.put("CMD", command)
            queryObject.put("Data", requestObj.toString())
            queryObject.put("Lang", mLocale)
            queryObject.put("TimeZone", Calendar.getInstance().timeZone.id)
            queryObject.put("TimeZoneOffSet", timezoneOffsetSt)
            queryObject.put("RequestTime", requestDate)
            queryObject.put("Sign", signedString)
            queryObject.put("CompanyId", companyID)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(TAG, "Request string - $queryObject")

        mParams = HashMap()
        mParams["query"] = queryObject.toString()
        retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    override fun getParams(): Map<String, String> {
        return mParams
    }

    public override fun parseNetworkResponse(
            response: NetworkResponse): Response<ServerResponse> {
        try {
            val jsonString = String(response.data,
                    HttpHeaderParser.parseCharset(response.headers))
            Log.d(TAG, "Response string - $jsonString")

            val jsonObject = JSONObject(jsonString)
            val responseObj = ServerResponse()

            val code = jsonObject.getInt("Code")
            if (code == ServerResponse.Status.SUCCESS) {
                responseObj.dataSt = jsonObject.getString("Data")
                responseObj.code = code
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response))
            } else if (code == ServerResponse.Status.NO_DATA) {
                responseObj.message = mContext.getString(R.string.no_data)
                responseObj.code = code
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response))
            } else if (code == ServerResponse.Status.Account_PasswordError) {
                responseObj.message = mContext.getString(R.string.wrong_password)
                responseObj.code = code
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response))
            } else if (code == ServerResponse.Status.Account_EmailAlreadyExit) {
                responseObj.message = mContext.getString(R.string.email_exist)
                responseObj.code = code
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response))
            } else if (code == ServerResponse.Status.INSUFFICIENT_CREDIT) {
                responseObj.message = jsonObject.getString("Message")
                responseObj.code = code
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response))
            } else {
                var serverError: ServerError
                try {
                    serverError = ServerError(jsonObject
                            .getJSONObject("Message"))
                } catch (jse: JSONException) {
                    serverError = ServerError(ServerError.Code.JSON_EXCEPTION, jsonObject.getString("Message"))
                }

                return Response.error(serverError)
            }
        } catch (uee: UnsupportedEncodingException) {
            Log.e(TAG, "Unsupported Encoding Exception", uee)
            return Response.error(ParseError(uee))
        } catch (je: JSONException) {
            Log.e(TAG, "JSON Exception", je)
            return Response.error(ServerError(ServerError.Code.JSON_EXCEPTION, "JSON exception"))
        }

    }

    override fun parseNetworkError(volleyError: VolleyError): VolleyError {
        val errorCode = if (volleyError.networkResponse != null) volleyError.networkResponse.statusCode else ServerError.Code.NETWORK_ERROR
        Log.e(TAG, "Parse Network Error with error code - $errorCode", volleyError)
        return ServerError(errorCode, "Parse Network Error")
    }

    override fun deliverResponse(response: ServerResponse) {
        mListener?.onResponse(response)
    }

    companion object {
        private val TAG = "ServerRequest"
    }
}