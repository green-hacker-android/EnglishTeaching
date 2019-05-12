package inc.osbay.android.tutorroom.sdk.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.sql.SQLException

class DataBaseHelper(private val mContext: Context) : SQLiteOpenHelper(mContext, DB_NAME, null, DB_VERSION) {
    private var mDataBase: SQLiteDatabase? = null

    init {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = mContext.applicationInfo.dataDir + "/databases/"
        } else {
            DB_PATH = "/data/data/" + mContext.packageName + "/databases/"
        }
    }// 1? Its database Version

    @Throws(IOException::class)
    fun createDataBase() {
        //If the database does not exist, copy it from the assets.

        val mDataBaseExist = checkDataBase()
        if (!mDataBaseExist) {
            this.readableDatabase
            this.close()
            try {
                //Copy the database from assests
                copyDataBase()
                Log.e(TAG, "createDatabase database created")
            } catch (mIOException: IOException) {
                throw Error("ErrorCopyingDataBase")
            }

        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private fun checkDataBase(): Boolean {
        val dbFile = File(DB_PATH + DB_NAME)
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists()
    }

    //Copy the database from assets
    @Throws(IOException::class)
    private fun copyDataBase() {
        val mInput = mContext.assets.open(DB_NAME)
        val outFileName = DB_PATH + DB_NAME
        val mOutput = FileOutputStream(outFileName)
        val mBuffer = ByteArray(1024)
        var mLength: Int
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength)
        }
        mOutput.flush()
        mOutput.close()
        mInput.close()
    }

    //Open the database, so we can query it
    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        val mPath = DB_PATH + DB_NAME
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY)
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null
    }

    @Synchronized
    override fun close() {
        if (mDataBase != null)
            mDataBase!!.close()
        super.close()
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        Log.d(TAG, "onCreate Database.")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        Log.d(TAG, "onUpgrade Database.")
        val dbFile = File(DB_PATH + DB_NAME)
        if (dbFile.exists()) {
            dbFile.delete()
        }

        try {
            copyDataBase()
        } catch (e: IOException) {
            Log.e(TAG, "Database upgrade fail.")
        }

    }

    companion object {
        private val DB_NAME = "tutorroom.db"// Database name
        //todo change db number
        private val DB_VERSION = 1
        private val TAG = "DataBaseHelper" // Tag just for the LogCat window
        //destination path (location) of our database on device
        private var DB_PATH = ""
    }
}