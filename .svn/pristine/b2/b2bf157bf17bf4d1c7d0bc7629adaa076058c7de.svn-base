package inc.osbay.android.tutorroom.sdk.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import inc.osbay.android.tutorroom.sdk.R;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;

import static inc.osbay.android.tutorroom.sdk.util.LGCUtil.md5;

public class ServerRequest extends Request<ServerResponse> {
    private static final String TAG = "ServerRequest";
    private Context mContext;
    private Response.Listener<ServerResponse> mListener;
    private Map<String, String> mParams;

    /**
     * Constructor with json object parameter.
     *
     * @param requestObj       Request object to parse to server.
     * @param responseListener Server response listener.
     * @param errorListener    Request error listener.
     */
    public ServerRequest(Context context, String command, JSONObject requestObj,
                         Response.Listener<ServerResponse> responseListener,
                         Response.ErrorListener errorListener) {
        super(Method.POST, CommonConstant.WEB_SERVICE_URL, errorListener);

        mContext = context;
        this.mListener = responseListener;
        JSONObject queryObject = new JSONObject();
        try {
            String mLocale = Locale.getDefault().getLanguage();
            String encodedString = LGCUtil.base64(requestObj.toString());
            // 2. Buildup the sign string
            DBAdapter DBAdapter = new DBAdapter(mContext);
            String companyID = DBAdapter.getCompanyID();
            String requestDate = LGCUtil.getCurrentUTCTimeString();
            String signSimple = companyID + requestDate + command + encodedString;
            signSimple = signSimple.trim();
            String signedString = md5(signSimple);
            Log.d("TEST", "signed Value = " + signedString);

            String timezoneOffsetSt = String.valueOf((double) Calendar.getInstance().getTimeZone().getRawOffset() / 3600000);
            queryObject.put("CMD", command);
            queryObject.put("Data", requestObj.toString());
            queryObject.put("Lang", mLocale);
            queryObject.put("TimeZone", Calendar.getInstance().getTimeZone().getID());
            queryObject.put("TimeZoneOffSet", timezoneOffsetSt);
            queryObject.put("RequestTime", requestDate);
            queryObject.put("Sign", signedString);
            queryObject.put("CompanyId", companyID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Request string - " + queryObject.toString());

        mParams = new HashMap<>();
        mParams.put("query", queryObject.toString());
        setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected final Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public final Response<ServerResponse> parseNetworkResponse(
            NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d(TAG, "Response string - " + jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);
            ServerResponse responseObj = new ServerResponse();

            int code = jsonObject.getInt("Code");
            if (code == ServerResponse.Status.SUCCESS) {
                responseObj.setDataSt(jsonObject.getString("Data"));
                responseObj.setCode(code);
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else if (code == ServerResponse.Status.NO_DATA) {
                responseObj.setMessage(mContext.getString(R.string.no_data));
                responseObj.setCode(code);
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else if (code == ServerResponse.Status.Account_PasswordError) {
                responseObj.setMessage(mContext.getString(R.string.wrong_password));
                responseObj.setCode(code);
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else if (code == ServerResponse.Status.Account_EmailAlreadyExit) {
                responseObj.setMessage(mContext.getString(R.string.email_exist));
                responseObj.setCode(code);
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            }  else if (code == ServerResponse.Status.INSUFFICIENT_CREDIT) {
                responseObj.setMessage(jsonObject.getString("Message"));
                responseObj.setCode(code);
                return Response.success(responseObj,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                ServerError serverError;
                try {
                    serverError = new ServerError(jsonObject
                            .getJSONObject("Message"));
                } catch (JSONException jse) {
                    serverError = new ServerError(ServerError.Code.JSON_EXCEPTION, jsonObject.getString("Message"));
                }
                return Response.error(serverError);
            }
        } catch (UnsupportedEncodingException uee) {
            Log.e(TAG, "Unsupported Encoding Exception", uee);
            return Response.error(new ParseError(uee));
        } catch (JSONException je) {
            Log.e(TAG, "JSON Exception", je);
            return Response.error(new ServerError(ServerError.Code.JSON_EXCEPTION, "JSON exception"));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        int errorCode = ((volleyError.networkResponse != null) ? volleyError.networkResponse.statusCode : ServerError.Code.NETWORK_ERROR);
        Log.e(TAG, "Parse Network Error with error code - " + errorCode, volleyError);
        return new ServerError(errorCode, "Parse Network Error");
    }

    @Override
    protected final void deliverResponse(ServerResponse response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
}