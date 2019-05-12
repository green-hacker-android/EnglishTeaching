package inc.osbay.android.tutorroom.sdk

import android.content.Context
import android.util.Log
import java.io.IOException
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DataBaseHelper
import inc.osbay.android.tutorroom.sdk.listener.StatusListener

object TRSDK {
    private val TAG = TRSDK::class.java!!.getSimpleName()

    fun initialize(mContext: Context, listener: StatusListener) {
        val mDbHelper = DataBaseHelper(mContext)
        try {
            mDbHelper.createDataBase()

            mDbHelper.close()

            listener.onSuccess()
        } catch (e: IOException) {
            Log.e(TAG, "Database create fail.", e)
            listener.onError(CommonConstant.DB_CREATE_FAIL)
        }

    }
}
