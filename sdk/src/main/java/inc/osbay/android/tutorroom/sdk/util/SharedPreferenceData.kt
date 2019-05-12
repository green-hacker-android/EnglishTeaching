package inc.osbay.android.tutorroom.sdk.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceData(context: Context) {
    internal var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences("tutor_room", Context.MODE_PRIVATE)
    }

    fun addBoolean(key: String, value: Boolean?) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putBoolean(key, value!!)
        prefsEditor.apply()
    }

    fun addString(key: String, value: String) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun addInt(key: String, value: Int) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.apply()
    }

    fun addDouble(key: String, value: Double) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        prefsEditor.apply()
    }

    fun remove(key: String) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.remove(key)
        prefsEditor.apply()
    }

    fun getDouble(key: String): Double {
        return if (sharedPreferences != null) {
            java.lang.Double.longBitsToDouble(sharedPreferences!!.getLong(key, 0))
        } else 0.0
    }

    fun getBoolean(key: String): Boolean {
        return if (sharedPreferences != null) {
            sharedPreferences!!.getBoolean(key, false)
        } else false
    }

    fun getString(key: String): String? {
        return if (sharedPreferences != null) {
            sharedPreferences!!.getString(key, "")
        } else ""
    }

    fun getInt(key: String): Int {
        return if (sharedPreferences != null) {
            sharedPreferences!!.getInt(key, 0)
        } else 0
    }
}
