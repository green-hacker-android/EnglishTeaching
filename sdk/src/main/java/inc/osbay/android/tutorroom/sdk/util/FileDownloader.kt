package inc.osbay.android.tutorroom.sdk.util

import android.os.AsyncTask
import android.util.Log

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant

object FileDownloader {
    private val TAG = FileDownloader::class.java!!.getSimpleName()

    private var downloadFinishedListener: OnDownloadFinishedListener? = null

    fun downloadImage(remoteUrl: String, listener: OnDownloadFinishedListener) {
        downloadFinishedListener = listener

        DownloadFileTask().execute(remoteUrl)
    }

    interface OnDownloadFinishedListener {
        fun onSuccess()

        fun onError()
    }

    private class DownloadFileTask : AsyncTask<String, Void, Boolean>() {
        override fun doInBackground(vararg urls: String): Boolean? {
            val videoUrl = urls[0]

            val mediaPath = File(CommonConstant.MEDIA_PATH)
            if (!mediaPath.exists())
                mediaPath.mkdirs()

            val fileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1, videoUrl.length)
            val file = File(mediaPath, fileName)

            try {
                if (urls.size == 1) {
                    val remoteUrl = URL(videoUrl)
                    Log.d(TAG, "Downloading file from - $remoteUrl")

                    val urlConnection = remoteUrl.openConnection()

                    urlConnection.connect()

                    val fileOutput = FileOutputStream(file)
                    val inputStream = BufferedInputStream(remoteUrl.openStream(), 8192)

                    val buffer = ByteArray(1024)
                    var bufferLength: Int

                    while ((bufferLength = inputStream.read(buffer) > 0) {
                        fileOutput.write(buffer, 0, bufferLength)
                    }
                    fileOutput.close()

                    return true
                }
            } catch (e: IOException) {
                e.printStackTrace()
                if (file.exists()) {
                    file.delete()
                }
            }

            return false
        }

        public override fun onPostExecute(result: Boolean?) {
            if (result!!) {
                if (downloadFinishedListener != null) {
                    downloadFinishedListener!!.onSuccess()
                }
            } else {
                if (downloadFinishedListener != null) {
                    downloadFinishedListener!!.onError()
                }
            }
        }
    }
}