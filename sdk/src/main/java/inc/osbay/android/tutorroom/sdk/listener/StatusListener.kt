package inc.osbay.android.tutorroom.sdk.listener

interface StatusListener {

    fun onSuccess()

    fun onError(errorCode: Int)
}