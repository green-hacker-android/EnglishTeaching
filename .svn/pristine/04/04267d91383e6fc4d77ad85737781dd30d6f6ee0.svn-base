package inc.osbay.android.tutorroom.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceData {
    SharedPreferences sharedPreferences;

    public SharedPreferenceData(Context context) {
        sharedPreferences = context.getSharedPreferences("tutor_room", Context.MODE_PRIVATE);
    }

    public void addBoolean(String key, Boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public void addString(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public void addInt(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

    public void addDouble(String key, double value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putLong(key, Double.doubleToRawLongBits(value));
        prefsEditor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.apply();
    }

    public double getDouble(String key) {
        if (sharedPreferences != null) {
            return Double.longBitsToDouble(sharedPreferences.getLong(key, 0));
        }
        return 0;
    }

    public boolean getBoolean(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, false);
        }
        return false;
    }

    public String getString(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public int getInt(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        }
        return 0;
    }
}
