package inc.osbay.android.tutorroom.sdk.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.model.Tag;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.sdk.util.SharedPreferenceData;

public class ServerRequestManager {
    public static final String TAG = ServerRequestManager.class.getSimpleName();
    private Context mContext;
    private RequestQueue mRequestQueue;

    public ServerRequestManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    /*** Get Company Configuration ***/
    public void getCompanyConfiguration(final OnRequestFinishedListener listener) {
        final SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(mContext);
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_COMPANYSETTING", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                try {
                    JSONObject settingObj = new JSONObject(response.getDataSt());
                    JSONArray configJsonArray = settingObj.getJSONArray("setting_list");
                    for (int j = 0; j < configJsonArray.length(); j++) {
                        JSONObject configObj = configJsonArray.getJSONObject(j);
                        if (configObj.has("CreditAmount"))
                            sharedPreferenceData.addDouble("credit_amount", Double.parseDouble((String) configObj.get("CreditAmount")));
                        else if (configObj.has("SingleClassCredit"))
                            sharedPreferenceData.addDouble("single_class_credit", Double.parseDouble((String) configObj.get("SingleClassCredit")));
                        else if (configObj.has("MultiClassCredit"))
                            sharedPreferenceData.addDouble("multiclass_credit", Double.parseDouble((String) configObj.get("MultiClassCredit")));
                        else if (configObj.has("ClassMinute"))
                            sharedPreferenceData.addInt("class_minute", Integer.parseInt((String) configObj.get("ClassMinute")));
                        else if (configObj.has("MinBookTime"))
                            sharedPreferenceData.addInt("min_book_time", Integer.parseInt((String) configObj.get("MinBookTime")));
                        else if (configObj.has("MaxBookTime"))
                            sharedPreferenceData.addInt("max_book_time", Integer.parseInt((String) configObj.get("MaxBookTime")));
                        else if (configObj.has("MinCancelTime"))
                            sharedPreferenceData.addInt("min_cancel_time", Integer.parseInt((String) configObj.get("MinCancelTime")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Twilio token and Websocket ***/
    public void getThirdPartConfig(final OnRequestFinishedListener listener) {
        final SharedPreferenceData preferenceData = new SharedPreferenceData(mContext);
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "GET_THIRD_PART_CONFIG", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                if (response.getCode() == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.getDataSt());
                        preferenceData.addString("TwilioAccountSid", jsonObject.getString("TwilioAccountSid"));
                        preferenceData.addString("TwilioAuthToken", jsonObject.getString("TwilioAuthToken"));
                        preferenceData.addString("TwilioApiKey", jsonObject.getString("TwilioApiKey"));
                        preferenceData.addString("TwilioApiSecret", jsonObject.getString("TwilioApiSecret"));
                        preferenceData.addString("BuyPhNumber", jsonObject.getString("BuyPhNumber"));
                        preferenceData.addString("WebSocketUrl", jsonObject.getString("WebSocketUrl"));
                        preferenceData.addString("WebSocketPort", jsonObject.getString("WebSocketPort"));
                        preferenceData.addString("WebSocketMode", jsonObject.getString("WebSocketMode"));
                        preferenceData.addString("PaypalAccId", jsonObject.getString("PaypalAccId"));
                        preferenceData.addString("PaypalSecrect", jsonObject.getString("PaypalSecrect"));
                        preferenceData.addString("Facebook", jsonObject.getString("Facebook"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listener.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Registering student via Email ***/
    public void registerStudent(String userName, String password, String email, String countryCode, String phone,
                                String registerType, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("user_name", userName);
            data.put("password", password);
            data.put("email", email);
            data.put("register_type", registerType);
            data.put("country_code", countryCode);
            data.put("phone", phone);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }

        ServerRequest request = new ServerRequest(mContext, "MB_REGISTER", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get student Info ***/
    public void getProfileInfo(String studentID, final OnRequestFinishedListener listener) {
        final SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(mContext);
        final DBAdapter dbAdapter = new DBAdapter(mContext);
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", studentID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }

        ServerRequest request = new ServerRequest(mContext, "MB_GET_ACCOUNT_INFO", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                Account account;
                try {
                    account = new Account(new JSONObject(response.getDataSt()));
                    dbAdapter.insertAccount(account);
                    sharedPreferenceData.addInt("account_id", account.getAccountId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Login student via Email ***/
    public void loginStudent(String password, String email,
                             final OnRequestFinishedListener listener) {
        final SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(mContext);
        final DBAdapter dbAdapter = new DBAdapter(mContext);
        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            data.put("email", email);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }

        ServerRequest request = new ServerRequest(mContext, "MB_LOGIN", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                if (response.getCode() == 1) {
                    try {
                        Account account = new Account(new JSONObject(response.getDataSt()));
                        dbAdapter.insertAccount(account);
                        sharedPreferenceData.addInt("account_id", account.getAccountId());
                        sharedPreferenceData.addString("account_name", account.getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listener.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Forget Password ***/
    public void forgetPassword(String email, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }

        ServerRequest request = new ServerRequest(mContext, "MB_FORGOT_PASSWORD", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Update student Info ***/
    public void updateProfileInfo(String userID, String userName, String countryCode, String phone,
                                  String email, String address, String country, String language,
                                  final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", userID);
            data.put("user_name", userName);
            data.put("country_code", countryCode);
            data.put("phone", phone);
            data.put("email", email);
            data.put("address", address);
            data.put("country", country);
            data.put("language", language);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }

        ServerRequest request = new ServerRequest(mContext, "MB_UPDATE_PROFILE_INFO", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Tutor List ***/
    public void getTutorList(final OnRequestFinishedListener listener) {
        JSONObject params = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_TUTORLIST", params, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
            }
        });

        mRequestQueue.add(request);
    }

    /*** Get Lesson List ***/
    public void getLessonListByAllTag(final OnRequestFinishedListener listener) {
        final DBAdapter dbAdapter = new DBAdapter(mContext);
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_LESSON_BY_AllTAG", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                if (response.getCode() == 1) {
                    JSONArray lessonJsonArray;
                    JSONArray tagJsonArray;
                    List<Lesson> lessonList = new ArrayList<>();
                    try {
                        tagJsonArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < tagJsonArray.length(); i++) {
                            Tag tag = new Tag(tagJsonArray.getJSONObject(i));
                            lessonJsonArray = tag.getLessonArray();
                            for (int j = 0; j < lessonJsonArray.length(); j++) {
                                Lesson lessonObj = new Lesson(lessonJsonArray.getJSONObject(j));
                                lessonList.add(lessonObj);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbAdapter.insertLessons(lessonList);
                    listener.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Lesson List by Packagee ID ***/
    public void getLessonListByPackageID(String packageID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("package_id", packageID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_LESSONLIST_BY_PACKAGE_ID", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Update Password ***/
    public void changePassword(String accountID, String oldPass, String newPass,
                               final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("old_password", oldPass);
            data.put("new_password", newPass);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_CHANGE_PASSWORD", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Packagee List ***/
    public void getPackageListByAllTag(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_PACKAGE_BY_AllTAG", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
                //listener.onSuccess(packageList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Account Credit ***/
    public void getAccountCredit(String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_ACCOUNT_CREDIT", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Tutor List ***/
    public void getTutorDetailByID(String tutorID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("tutor_id", tutorID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_TUTORDETAIL", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Language List ***/
    public void getLanguageList(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_LANGUAGELIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Price Tier ***/
    public void getPriceTier(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_PRICETIER", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Price Tier List ***/
    public void getPriceTierList(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_PRICETIER_LIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Buy Credit ***/
    public void buyCredit(String accountID, int buyType, String credit, String pricePackageID,
                          final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("buy_type", String.valueOf(buyType));
            if (buyType == CommonConstant.buyCredit)
                data.put("credit", credit);
            else if (buyType == CommonConstant.buyStorePackage)
                data.put("pricepackageId", pricePackageID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_BUY_CREDIT", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Country List ***/
    public void getCountryList(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_COUNTRYLIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Tag List ***/
    public void getTagList(String tagType, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("tag_type", tagType);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_TAG_LIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Twilio token and Websocket ***/
    public void getAllMultiplyClass(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "GET_ALL_MULTI_CLASS", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Packagee By Tag ID ***/
    public void getPackageByTagID(String tagID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("tag_id", tagID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_PACKAGE_BY_TAG_ID", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Lesson By Tag ID ***/
    public void getLessonByTagID(String tagID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("tag_id", tagID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_LESSON_BY_TAG_ID", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Available Tutor By Time ***/
    public void getAvailableTutorByTime(String startDate, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("start_date", startDate);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_TUTOR_AVAILABLETIME_BOOK", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
                /*listener.onError(new ServerError(ServerError.Code.JSON_EXCEPTION,
                        "Cannot parse Tutor Object."));*/
            }
        });
        mRequestQueue.add(request);
    }

    /*** Giving feedback ***/
    public void sendFeedback(String accountID, String note, String classID, String rate,
                             final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("notes", note);
            data.put("class_id", classID);
            data.put("rate", rate);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "CLASSROOM_ADD_STUDENT_RATEANDNOTE", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Generate Twilio Token ***/
    public void generateTwilioToken(String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "GENERATE_TWILIO_TOKEN", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** book Single Class ***/
    public void bookSingleClass(String accountID, String lessonID, String tutorID, String startDate,
                                String endDate, String bookingType, String lessonType, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("lesson_id", lessonID);
            data.put("tutor_id", tutorID);
            data.put("start_date", LGCUtil.convertToUTC(startDate));
            data.put("end_date", LGCUtil.convertToUTC(endDate));
            data.put("booking_type", bookingType);
            data.put("lesson_type", lessonType);
        } catch (JSONException | ParseException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_BOOKING_SINGLE_CLASS", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Booking List ***/
    public void getBookingList(String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_STUDENT_BOOKING_LIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Check Start Class ***/
    public void checkStartClass(String classID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("class_id", classID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "CHECK_START_CLASS", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Join Classroom ***/
    public void joinClassroom(String accountID, String boardID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("board_id", boardID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "CLASSROOM_JOIN", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Leave Classroom ***/
    public void leaveClassroom(String roomLockID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("gta_accountroomlog_id", roomLockID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "CLASSROOM_LEAVE", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Notification ***/
    public void getNoti(String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_ACCOUNT_NOTIFICATION_LIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Change/Read Notification Status ***/
    public void readNoti(String notiID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("noti_id", notiID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_CHANGE_STATUS_ACCOUNT_NOTIFICATION", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Room Code By Room ID ***/
    public void getRoomCodeByRoomID(String classID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("class_id", classID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "GET_ROOM_CODE_BY_ROOMID", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Online Call Centre Manager ***/
    public void getOnlineCallCentreManager(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_ONLINECALLCENTERMANAGER", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Save Online Support Message ***/
    public void saveOnlineSupportMessage(String callCenterID, String accountID, String messageContent, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("callcenter_id", callCenterID);
            data.put("account_id", accountID);
            data.put("message_content", messageContent);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_SAVE_ONLINESUPPORTMESSAGE", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Save Classroom Message ***/
    public void saveClassroomMessage(String classID, String accountID, String messageContent, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("class_id", classID);
            data.put("account_id", accountID);
            data.put("message_content", messageContent);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_CLASSROOM_SAVE_MESSAGE_HISTORY", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Update Message Status ***/
    public void updateMessageStatus(String callCenterID, String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("callcenter_id", callCenterID);
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_UPDATE_ONLINESUPPORTMESSAGE_STATUS", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Messages ***/
    public void getMessages(String callCenterID, final String accountID, final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        try {
            data.put("callcenter_id", callCenterID);
            data.put("account_id", accountID);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_GET_ONLINESUPPORTMESSAGE", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Get Banner ***/
    public void getBanner(final OnRequestFinishedListener listener) {
        JSONObject data = new JSONObject();
        ServerRequest request = new ServerRequest(mContext, "MB_GET_BANNERLIST", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /*** Update Avatar ***/
    public void updateAvatar(String accountID, String avatar,
                             final OnRequestFinishedListener listener) {
        final SharedPreferenceData preferenceData = new SharedPreferenceData(mContext);
        final DBAdapter dbAdapter = new DBAdapter(mContext);
        JSONObject data = new JSONObject();
        try {
            data.put("account_id", accountID);
            data.put("base64string", avatar);
        } catch (JSONException je) {
            Log.e(TAG, "Cannot create data object.", je);
        }
        ServerRequest request = new ServerRequest(mContext, "MB_UPDATE_AVATAR", data, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse response) {
                try {
                    Account account = new Account(new JSONObject(response.getDataSt()));
                    dbAdapter.insertAccount(account);
                    preferenceData.addInt("account_id", account.getAccountId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError((ServerError) error);
                Log.i("Error getting Version", String.valueOf(error));
            }
        });
        mRequestQueue.add(request);
    }

    /**
     * Listener to get callback for server request process.
     *
     * @author Ambrose
     */
    public interface OnRequestFinishedListener {
        /**
         * Callback method for success case.
         *
         * @param response Response object.
         */
        void onSuccess(ServerResponse response);

        /**
         * Callback method for error case.
         *
         * @param err Error object
         */
        void onError(ServerError err);
    }
}
