package inc.osbay.android.tutorroom

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.util.Log

import com.facebook.drawee.backends.pipeline.Fresco

import inc.osbay.android.tutorroom.sdk.TRSDK
import inc.osbay.android.tutorroom.sdk.listener.StatusListener
import inc.osbay.android.tutorroom.utils.WSMessageClient

class TRApplication : Application() {
    var wsMessageClient: WSMessageClient? = null
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize TutorMandarin SDK
        TRSDK.initialize(applicationContext, object : StatusListener {
            override fun onSuccess() {
                Log.d(TAG, "TutorRoom sdk initialized.")
            }

            override fun onError(errorCode: Int) {
                Log.e(TAG, "TutorRoom sdk initialize error.")
            }
        })


        wsMessageClient = WSMessageClient(applicationContext)
        //mWSMessageClient.doBindService();

        // Setup handler for crashing or uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler { thread, e -> handleUncaughtException(thread, e) }

        // Initialize Fresco SDK
        Fresco.initialize(applicationContext)
    }

    fun handleUncaughtException(thread: Thread, e: Throwable) {
        e.printStackTrace() // not all Android versions will print the stack trace automatically

        val intent = Intent()
        intent.action = "inc.osbay.android.tutorroom.SEND_LOG"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // required when starting from Application
        startActivity(intent)

        System.exit(1) // kill off the crashed app
    }

    override fun onTerminate() {
        super.onTerminate()
        wsMessageClient!!.doUnbindService()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private val TAG = TRApplication::class.java!!.getSimpleName()
        var instance: TRApplication? = null
            private set
    }
}
