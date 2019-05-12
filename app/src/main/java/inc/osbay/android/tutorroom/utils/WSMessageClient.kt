package inc.osbay.android.tutorroom.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import inc.osbay.android.tutorroom.service.MessengerService

class WSMessageClient(internal var mContext: Context) {
    internal var mService: Messenger? = null
    internal var mIsBound: Boolean = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            mService = Messenger(service)
            // TODO remote service connected.
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null

            // TODO remote service disconnected.
        }
    }

    fun addMessenger(messenger: Messenger) {
        if (mService != null) {
            try {
                val msg = Message.obtain(null,
                        MessengerService.MSG_REGISTER_CLIENT)
                msg.replyTo = messenger
                mService!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }
    }

    fun doBindService() {
        mContext.bindService(Intent(mContext,
                MessengerService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsBound = true
    }

    fun removeMessenger(messenger: Messenger) {
        if (mIsBound && mService != null) {
            try {
                val msg = Message.obtain(null,
                        MessengerService.MSG_UNREGISTER_CLIENT)
                msg.replyTo = messenger
                mService!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }
    }

    fun doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mContext.unbindService(mConnection)
            mIsBound = false
        }
    }

    @Throws(RemoteException::class)
    fun sendMessage(message: Message) {
        mService!!.send(message)
    }
}
