package inc.osbay.android.tutorroom.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

import inc.osbay.android.tutorroom.R

class CameraActivity : AppCompatActivity() {

    private var mCamera: Camera? = null

    private var mPreview: CameraPreview? = null

    private var mPicture: PictureCallback? = null

    private// get cameras number
    // get camerainfo
    val cameraInstance: Camera?
        get() {
            var cameraCount = 0
            var cam: Camera? = null

            val cameraInfo = Camera.CameraInfo()
            cameraCount = Camera.getNumberOfCameras()

            for (camIdx in 0..cameraCount) {
                Camera.getCameraInfo(camIdx, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        cam = Camera.open(camIdx)
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }

                }
            }

            if (cam == null) {
                try {
                    cam = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }

            }

            return cam
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val mediaUrl = intent.getStringExtra(
                android.provider.MediaStore.EXTRA_OUTPUT)
        Log.d(TAG, "Image url - $mediaUrl")

        // Create an instance of Camera
        mCamera = cameraInstance

        if (mCamera == null) {
            this.finish()
            return
        }

        mPicture = PictureCallback { data, camera ->
            val dir = File(mediaUrl.substring(0,
                    mediaUrl.lastIndexOf('/')))
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val pictureFile = File(mediaUrl)
            // if (pictureFile == null){
            // mLog.d(TAG,
            // "Error creating media file, check storage permissions: ");
            // return;
            // }

            try {
                val fos = FileOutputStream(pictureFile)
                fos.write(data)
                fos.close()
            } catch (e: FileNotFoundException) {
                Log.d(TAG, "File not found: " + e.message)
            } catch (e: IOException) {
                Log.d(TAG, "Error accessing file: " + e.message)
            }

            mCamera!!.release()
            mCamera = null

            val intent = Intent()
            intent.data = Uri.fromFile(pictureFile)

            this@CameraActivity.setResult(Activity.RESULT_OK, intent)
            this@CameraActivity.finish()
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = CameraPreview(this, mCamera)
        val preview = findViewById<FrameLayout>(R.id.fl_preview)
        preview.addView(mPreview)

        val captureButton = findViewById<TextView>(R.id.btn_shot)
        captureButton.setOnClickListener {
            // get an image from the camera
            mCamera!!.takePicture(null, null, mPicture)
        }
    }

    override fun onDestroy() {
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
        super.onDestroy()
    }

    internal inner class CameraPreview(context: Context, private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

        private val mHolder: SurfaceHolder

        private var mPreviewSize: Camera.Size? = null

        private val mSupportedPreviewSizes: List<Camera.Size>?

        init {

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = holder
            mHolder.addCallback(this)
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

            mSupportedPreviewSizes = mCamera.parameters
                    .supportedPreviewSizes
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = View.resolveSize(suggestedMinimumWidth,
                    widthMeasureSpec)
            val height = View.resolveSize(suggestedMinimumHeight,
                    heightMeasureSpec)
            setMeasuredDimension(width, height)

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
                        width,
                        height)
            }
        }

        private fun getOptimalPreviewSize(sizes: List<Camera.Size>?,
                                          w: Int,
                                          h: Int): Camera.Size? {
            val ASPECT_TOLERANCE = 0.1
            val targetRatio = h.toDouble() / w

            if (sizes == null) {
                return null
            }

            var optimalSize: Camera.Size? = null
            var minDiff = java.lang.Double.MAX_VALUE

            for (size in sizes) {
                val ratio = size.width.toDouble() / size.height
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                    continue
                }

                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }

            if (optimalSize == null) {
                minDiff = java.lang.Double.MAX_VALUE
                for (size in sizes) {
                    if (Math.abs(size.height - h) < minDiff) {
                        optimalSize = size
                        minDiff = Math.abs(size.height - h).toDouble()
                    }
                }
            }
            return optimalSize
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            // The Surface has been created, now tell the camera where to draw
            // the
            // preview.
            try {
                mCamera.setPreviewDisplay(holder)
                mCamera.setDisplayOrientation(90)

                val parameters = mCamera.parameters
                parameters.setPreviewSize(mPreviewSize!!.width,
                        mPreviewSize!!.height)
                mCamera.parameters = parameters
                mCamera.startPreview()

            } catch (e: IOException) {
                Log.d(TAG, "Error setting camera preview: " + e.message)
            }

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            // empty. Take care of releasing the Camera preview in your
            // activity.
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int,
                                    h: Int) {
            // If your preview can change or rotate, take care of those events
            // here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.surface == null) {
                // preview surface does not exist
                return
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview()
            } catch (e: Exception) {
                // ignore: tried to stop a non-existent preview
            }

            try {
                mCamera.setPreviewDisplay(mHolder)
                mCamera.startPreview()

            } catch (e: Exception) {
                Log.d(TAG, "Error starting camera preview: " + e.message)
            }

        }

        companion object {
            private val TAG = "CameraPreview"
        }
    }

    companion object {
        private val TAG = "CameraActivity"
    }
}
