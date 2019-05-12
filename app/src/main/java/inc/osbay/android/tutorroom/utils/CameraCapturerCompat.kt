package inc.osbay.android.tutorroom.utils

import android.content.Context
import android.util.Log
import android.util.Pair
import com.twilio.video.Camera2Capturer
import com.twilio.video.CameraCapturer
import com.twilio.video.VideoCapturer
import org.webrtc.Camera2Enumerator

/*
 * Simple wrapper class that uses Camera2Capturer with supported devices.
 */
class CameraCapturerCompat(context: Context,
                           cameraSource: CameraCapturer.CameraSource) {

    private var camera1Capturer: CameraCapturer? = null
    private var camera2Capturer: Camera2Capturer? = null
    private var frontCameraPair: Pair<CameraCapturer.CameraSource, String>? = null
    private var backCameraPair: Pair<CameraCapturer.CameraSource, String>? = null

    val cameraSource: CameraCapturer.CameraSource
        get() = if (usingCamera1()) {
            camera1Capturer!!.cameraSource
        } else {
            getCameraSource(camera2Capturer!!.cameraId)
        }

    /*
     * This method is required because this class is not an implementation of VideoCapturer due to
     * a shortcoming in the Video Android SDK.
     */
    val videoCapturer: VideoCapturer?
        get() = if (usingCamera1()) {
            camera1Capturer
        } else {
            camera2Capturer
        }

    init {
        if (Camera2Capturer.isSupported(context)) {
            setCameraPairs(context)
            val camera2Listener = object : Camera2Capturer.Listener {
                override fun onFirstFrameAvailable() {
                    Log.i(TAG, "onFirstFrameAvailable")
                }

                override fun onCameraSwitched(newCameraId: String) {
                    Log.i(TAG, "onCameraSwitched: newCameraId = $newCameraId")
                }

                override fun onError(camera2CapturerException: Camera2Capturer.Exception) {
                    Log.e(TAG, camera2CapturerException.message)
                }
            }
            camera2Capturer = Camera2Capturer(context,
                    getCameraId(cameraSource),
                    camera2Listener)
        } else {
            camera1Capturer = CameraCapturer(context, cameraSource)
        }
    }

    fun switchCamera() {
        if (usingCamera1()) {
            camera1Capturer!!.switchCamera()
        } else {
            val cameraSource = getCameraSource(camera2Capturer!!
                    .cameraId)

            if (cameraSource == CameraCapturer.CameraSource.FRONT_CAMERA) {
                camera2Capturer!!.switchCamera(backCameraPair!!.second)
            } else {
                camera2Capturer!!.switchCamera(frontCameraPair!!.second)
            }
        }
    }

    private fun usingCamera1(): Boolean {
        return camera1Capturer != null
    }

    private fun setCameraPairs(context: Context) {
        val camera2Enumerator = Camera2Enumerator(context)
        for (cameraId in camera2Enumerator.deviceNames) {
            if (camera2Enumerator.isFrontFacing(cameraId)) {
                frontCameraPair = Pair<CameraSource, String>(CameraCapturer.CameraSource.FRONT_CAMERA, cameraId)
            }
            if (camera2Enumerator.isBackFacing(cameraId)) {
                backCameraPair = Pair<CameraSource, String>(CameraCapturer.CameraSource.BACK_CAMERA, cameraId)
            }
        }
    }

    private fun getCameraId(cameraSource: CameraCapturer.CameraSource): String {
        return if (frontCameraPair!!.first == cameraSource) {
            frontCameraPair!!.second
        } else {
            backCameraPair!!.second
        }
    }

    private fun getCameraSource(cameraId: String): CameraCapturer.CameraSource {
        return if (frontCameraPair!!.second == cameraId) {
            frontCameraPair!!.first
        } else {
            backCameraPair!!.first
        }
    }

    companion object {
        private val TAG = "CameraCapturerCompat"
    }
}
