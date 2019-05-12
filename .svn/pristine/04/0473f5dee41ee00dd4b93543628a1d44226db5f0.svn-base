package inc.osbay.android.tutorroom.sdk;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DataBaseHelper;
import inc.osbay.android.tutorroom.sdk.listener.StatusListener;

public class TRSDK {
    private static final String TAG = TRSDK.class.getSimpleName();

    public static void initialize(Context mContext, StatusListener listener) {
        DataBaseHelper mDbHelper = new DataBaseHelper(mContext);
        try {
            mDbHelper.createDataBase();

            mDbHelper.close();

            listener.onSuccess();
        } catch (IOException e) {
            Log.e(TAG, "Database create fail.", e);
            listener.onError(CommonConstant.DB_CREATE_FAIL);
        }
    }
}
