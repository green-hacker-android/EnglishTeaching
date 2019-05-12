package inc.osbay.android.tutorroom.sdk.client

import android.content.Context
import android.util.Log

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.ParseException
import java.util.ArrayList

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.Tag
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.sdk.util.SharedPreferenceData

class ServerRequestManager(private val mContext: Context) {
    private val mRequestQueue: RequestQueue

    init {
        mRequestQueue = Volley.newRequestQueue(mContext)
    }

    /*** Get Company Configuration  */
    fun getCompanyConfiguration(listener: OnRequestFinishedListener) {
        val sharedPreferenceData = SharedPreferenceData(mContext)
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_COMPANYSETTING", data, Response.Listener { response ->
            try {
                val settingObj = JSONObject(response.dataSt)
                val configJsonArray = settingObj.getJSONArray("setting_list")
                for (j in 0 until configJsonArray.length()) {
                    val configObj = configJsonArray.getJSONObject(j)
                    if (configObj.has("CreditAmount"))
                        sharedPreferenceData.addDouble("credit_amount", java.lang.Double.parseDouble(configObj.get("CreditAmount") as String))
                    else if (configObj.has("SingleClassCredit"))
                        sharedPreferenceData.addDouble("single_class_credit", java.lang.Double.parseDouble(configObj.get("SingleClassCredit") as String))
                    else if (configObj.has("MultiClassCredit"))
                        sharedPreferenceData.addDouble("multiclass_credit", java.lang.Double.parseDouble(configObj.get("MultiClassCredit") as String))
                    else if (configObj.has("ClassMinute"))
                        sharedPreferenceData.addInt("class_minute", Integer.parseInt(configObj.get("ClassMinute") as String))
                    else if (configObj.has("MinBookTime"))
                        sharedPreferenceData.addInt("min_book_time", Integer.parseInt(configObj.get("MinBookTime") as String))
                    else if (configObj.has("MaxBookTime"))
                        sharedPreferenceData.addInt("max_book_time", Integer.parseInt(configObj.get("MaxBookTime") as String))
                    else if (configObj.has("MinCancelTime"))
                        sharedPreferenceData.addInt("min_cancel_time", Integer.parseInt(configObj.get("MinCancelTime") as String))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            listener.onSuccess(null)
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Twilio token and Websocket  */
    fun getThirdPartConfig(listener: OnRequestFinishedListener) {
        val preferenceData = SharedPreferenceData(mContext)
        val data = JSONObject()
        val request = ServerRequest(mContext, "GET_THIRD_PART_CONFIG", data, Response.Listener { response ->
            if (response.code == 1) {
                try {
                    val jsonObject = JSONObject(response.dataSt)
                    preferenceData.addString("TwilioAccountSid", jsonObject.getString("TwilioAccountSid"))
                    preferenceData.addString("TwilioAuthToken", jsonObject.getString("TwilioAuthToken"))
                    preferenceData.addString("TwilioApiKey", jsonObject.getString("TwilioApiKey"))
                    preferenceData.addString("TwilioApiSecret", jsonObject.getString("TwilioApiSecret"))
                    preferenceData.addString("BuyPhNumber", jsonObject.getString("BuyPhNumber"))
                    preferenceData.addString("WebSocketUrl", jsonObject.getString("WebSocketUrl"))
                    preferenceData.addString("WebSocketPort", jsonObject.getString("WebSocketPort"))
                    preferenceData.addString("WebSocketMode", jsonObject.getString("WebSocketMode"))
                    preferenceData.addString("PaypalAccId", jsonObject.getString("PaypalAccId"))
                    preferenceData.addString("PaypalSecrect", jsonObject.getString("PaypalSecrect"))
                    preferenceData.addString("Facebook", jsonObject.getString("Facebook"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                listener.onSuccess(null)
            }
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Registering student via Email  */
    fun registerStudent(userName: String, password: String, email: String, countryCode: String, phone: String,
                        registerType: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("user_name", userName)
            data.put("password", password)
            data.put("email", email)
            data.put("register_type", registerType)
            data.put("country_code", countryCode)
            data.put("phone", phone)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_REGISTER", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get student Info  */
    fun getProfileInfo(studentID: String, listener: OnRequestFinishedListener) {
        val sharedPreferenceData = SharedPreferenceData(mContext)
        val dbAdapter = DBAdapter(mContext)
        val data = JSONObject()
        try {
            data.put("account_id", studentID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_ACCOUNT_INFO", data, Response.Listener { response ->
            val account: Account
            try {
                account = Account(JSONObject(response.dataSt))
                dbAdapter.insertAccount(account)
                sharedPreferenceData.addInt("account_id", account.accountId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            listener.onSuccess(response)
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Login student via Email  */
    fun loginStudent(password: String, email: String,
                     listener: OnRequestFinishedListener) {
        val sharedPreferenceData = SharedPreferenceData(mContext)
        val dbAdapter = DBAdapter(mContext)
        val data = JSONObject()
        try {
            data.put("password", password)
            data.put("email", email)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_LOGIN", data, Response.Listener { response ->
            if (response.code == 1) {
                try {
                    val account = Account(JSONObject(response.dataSt))
                    dbAdapter.insertAccount(account)
                    sharedPreferenceData.addInt("account_id", account.accountId)
                    sharedPreferenceData.addString("account_name", account.name!!)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                listener.onSuccess(response)
            }
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Forget Password  */
    fun forgetPassword(email: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("email", email)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_FORGOT_PASSWORD", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Update student Info  */
    fun updateProfileInfo(userID: String, userName: String, countryCode: String, phone: String,
                          email: String, address: String, country: String, language: String,
                          listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", userID)
            data.put("user_name", userName)
            data.put("country_code", countryCode)
            data.put("phone", phone)
            data.put("email", email)
            data.put("address", address)
            data.put("country", country)
            data.put("language", language)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_UPDATE_PROFILE_INFO", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Tutor List  */
    fun getTutorList(listener: OnRequestFinishedListener) {
        val params = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_TUTORLIST", params, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error -> listener.onError(error as ServerError) })

        mRequestQueue.add(request)
    }

    /*** Get Lesson List  */
    fun getLessonListByAllTag(listener: OnRequestFinishedListener) {
        val dbAdapter = DBAdapter(mContext)
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_LESSON_BY_AllTAG", data, Response.Listener { response ->
            if (response.code == 1) {
                var lessonJsonArray: JSONArray?
                val tagJsonArray: JSONArray
                val lessonList = ArrayList<Lesson>()
                try {
                    tagJsonArray = JSONArray(response.dataSt)
                    for (i in 0 until tagJsonArray.length()) {
                        val tag = Tag(tagJsonArray.getJSONObject(i))
                        lessonJsonArray = tag.lessonArray
                        for (j in 0 until lessonJsonArray!!.length()) {
                            val lessonObj = Lesson(lessonJsonArray.getJSONObject(j))
                            lessonList.add(lessonObj)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                dbAdapter.insertLessons(lessonList)
                listener.onSuccess(null)
            }
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Lesson List by Packagee ID  */
    fun getLessonListByPackageID(packageID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("package_id", packageID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_LESSONLIST_BY_PACKAGE_ID", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Update Password  */
    fun changePassword(accountID: String, oldPass: String, newPass: String,
                       listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("old_password", oldPass)
            data.put("new_password", newPass)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_CHANGE_PASSWORD", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Packagee List  */
    fun getPackageListByAllTag(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_PACKAGE_BY_AllTAG", data, Response.Listener { response ->
            listener.onSuccess(response)
            //listener.onSuccess(packageList);
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Account Credit  */
    fun getAccountCredit(accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_ACCOUNT_CREDIT", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Tutor List  */
    fun getTutorDetailByID(tutorID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("tutor_id", tutorID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_TUTORDETAIL", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Language List  */
    fun getLanguageList(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_LANGUAGELIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Price Tier  */
    fun getPriceTier(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_PRICETIER", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Price Tier List  */
    fun getPriceTierList(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_PRICETIER_LIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Buy Credit  */
    fun buyCredit(accountID: String, buyType: Int, credit: String, pricePackageID: String,
                  listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("buy_type", buyType.toString())
            if (buyType == CommonConstant.buyCredit)
                data.put("credit", credit)
            else if (buyType == CommonConstant.buyStorePackage)
                data.put("pricepackageId", pricePackageID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_BUY_CREDIT", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Country List  */
    fun getCountryList(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_COUNTRYLIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Tag List  */
    fun getTagList(tagType: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("tag_type", tagType)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_TAG_LIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Twilio token and Websocket  */
    fun getAllMultiplyClass(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "GET_ALL_MULTI_CLASS", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Packagee By Tag ID  */
    fun getPackageByTagID(tagID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("tag_id", tagID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_PACKAGE_BY_TAG_ID", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Lesson By Tag ID  */
    fun getLessonByTagID(tagID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("tag_id", tagID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_LESSON_BY_TAG_ID", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Available Tutor By Time  */
    fun getAvailableTutorByTime(startDate: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("start_date", startDate)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_TUTOR_AVAILABLETIME_BOOK", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
            /*listener.onError(new ServerError(ServerError.Code.JSON_EXCEPTION,
                "Cannot parse Tutor Object."));*/
        })
        mRequestQueue.add(request)
    }

    /*** Giving feedback  */
    fun sendFeedback(accountID: String, note: String, classID: String, rate: String,
                     listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("notes", note)
            data.put("class_id", classID)
            data.put("rate", rate)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "CLASSROOM_ADD_STUDENT_RATEANDNOTE", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Generate Twilio Token  */
    fun generateTwilioToken(accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "GENERATE_TWILIO_TOKEN", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** book Single Class  */
    fun bookSingleClass(accountID: String, lessonID: String, tutorID: String, startDate: String,
                        endDate: String, bookingType: String, lessonType: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("lesson_id", lessonID)
            data.put("tutor_id", tutorID)
            data.put("start_date", LGCUtil.convertToUTC(startDate))
            data.put("end_date", LGCUtil.convertToUTC(endDate))
            data.put("booking_type", bookingType)
            data.put("lesson_type", lessonType)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        } catch (je: ParseException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_BOOKING_SINGLE_CLASS", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Booking List  */
    fun getBookingList(accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_STUDENT_BOOKING_LIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Check Start Class  */
    fun checkStartClass(classID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("class_id", classID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "CHECK_START_CLASS", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Join Classroom  */
    fun joinClassroom(accountID: String, boardID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("board_id", boardID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "CLASSROOM_JOIN", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Leave Classroom  */
    fun leaveClassroom(roomLockID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("gta_accountroomlog_id", roomLockID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "CLASSROOM_LEAVE", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Notification  */
    fun getNoti(accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_ACCOUNT_NOTIFICATION_LIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Change/Read Notification Status  */
    fun readNoti(notiID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("noti_id", notiID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_CHANGE_STATUS_ACCOUNT_NOTIFICATION", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Room Code By Room ID  */
    fun getRoomCodeByRoomID(classID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("class_id", classID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "GET_ROOM_CODE_BY_ROOMID", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Online Call Centre Manager  */
    fun getOnlineCallCentreManager(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_ONLINECALLCENTERMANAGER", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Save Online Support Message  */
    fun saveOnlineSupportMessage(callCenterID: String, accountID: String, messageContent: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("callcenter_id", callCenterID)
            data.put("account_id", accountID)
            data.put("message_content", messageContent)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_SAVE_ONLINESUPPORTMESSAGE", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Save Classroom Message  */
    fun saveClassroomMessage(classID: String, accountID: String, messageContent: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("class_id", classID)
            data.put("account_id", accountID)
            data.put("message_content", messageContent)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_CLASSROOM_SAVE_MESSAGE_HISTORY", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Update Message Status  */
    fun updateMessageStatus(callCenterID: String, accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("callcenter_id", callCenterID)
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_UPDATE_ONLINESUPPORTMESSAGE_STATUS", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Messages  */
    fun getMessages(callCenterID: String, accountID: String, listener: OnRequestFinishedListener) {
        val data = JSONObject()
        try {
            data.put("callcenter_id", callCenterID)
            data.put("account_id", accountID)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_GET_ONLINESUPPORTMESSAGE", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Get Banner  */
    fun getBanner(listener: OnRequestFinishedListener) {
        val data = JSONObject()
        val request = ServerRequest(mContext, "MB_GET_BANNERLIST", data, Response.Listener { response -> listener.onSuccess(response) }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /*** Update Avatar  */
    fun updateAvatar(accountID: String, avatar: String,
                     listener: OnRequestFinishedListener) {
        val preferenceData = SharedPreferenceData(mContext)
        val dbAdapter = DBAdapter(mContext)
        val data = JSONObject()
        try {
            data.put("account_id", accountID)
            data.put("base64string", avatar)
        } catch (je: JSONException) {
            Log.e(TAG, "Cannot create data object.", je)
        }

        val request = ServerRequest(mContext, "MB_UPDATE_AVATAR", data, Response.Listener { response ->
            try {
                val account = Account(JSONObject(response.dataSt))
                dbAdapter.insertAccount(account)
                preferenceData.addInt("account_id", account.accountId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            listener.onSuccess(response)
        }, Response.ErrorListener { error ->
            listener.onError(error as ServerError)
            Log.i("Error getting Version", error.toString())
        })
        mRequestQueue.add(request)
    }

    /**
     * Listener to get callback for server request process.
     *
     * @author Ambrose
     */
    interface OnRequestFinishedListener {
        /**
         * Callback method for success case.
         *
         * @param response Response object.
         */
        fun onSuccess(response: ServerResponse?)

        /**
         * Callback method for error case.
         *
         * @param err Error object
         */
        fun onError(err: ServerError)
    }

    companion object {
        val TAG = ServerRequestManager::class.java.simpleName
    }
}
