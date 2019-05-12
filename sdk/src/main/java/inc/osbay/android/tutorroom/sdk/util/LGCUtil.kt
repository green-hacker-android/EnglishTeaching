package inc.osbay.android.tutorroom.sdk.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant

object LGCUtil {
    val TEMP_PHOTO_URL = CommonConstant.IMAGE_PATH + File.separator + "temp.jpg"
    var FORMAT_NOTIME = "yyyy-MM-dd"
    var FORMAT_WEEKDAY = "EE, MMM dd, yyyy"
    var FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss"
    var FORMAT_LONG_WEEKDAY = "yyyy MMMM dd"
    var FORMAT_TIME_NO_SEC = "HH:mm"

    /*** Get Current Time in UTC format  */
    val currentUTCTimeString: String
        get() {
            val dateFormat = SimpleDateFormat(CommonConstant.DATE_TIME_FORMAT,
                    Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.format(Date())
        }

    val currentTimeString: String
        get() {
            val dateFormat = SimpleDateFormat(FORMAT_NOTIME,
                    Locale.getDefault())
            return dateFormat.format(Date())
        }

    fun getGenderName(genderType: Int): String {
        return if (genderType == 1) {
            "Male"
        } else if (genderType == 2) {
            "Female"
        } else {
            ""
        }
    }

    /*** Get Date Time in UTC format  */
    @Throws(ParseException::class)
    fun convertToUTC(localDateString: String): String {
        val dateFormat = SimpleDateFormat(FORMAT_NORMAL, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        val localDate = dateFormat.parse(localDateString)

        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(localDate)
    }

    @Throws(ParseException::class)
    fun convertToUTC(localDateString: String, format: String): String {
        var dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        val localDate = dateFormat.parse(localDateString)

        dateFormat = SimpleDateFormat(FORMAT_NORMAL, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(localDate)
    }

    @Throws(ParseException::class)
    fun convertToNoTime(utcDateString: String): String {
        val dateFormat = SimpleDateFormat(FORMAT_NOTIME, Locale.getDefault())
        val localDate = dateFormat.parse(utcDateString)
        return dateFormat.format(localDate)
    }

    @Throws(ParseException::class)
    fun convertToLocale(utcDateString: String, oriDateFormat: String): String {
        val dateFormat = SimpleDateFormat(oriDateFormat, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val localDate = dateFormat.parse(utcDateString)

        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(localDate)
    }

    /*** String Date to miliseconds  */
    @Throws(ParseException::class)
    fun dateToMilisecond(dateString: String, selectedFormat: String): Long {
        val dateFormat = SimpleDateFormat(selectedFormat, Locale.getDefault())
        val localDate = dateFormat.parse(dateString)
        return localDate.time
    }

    @Throws(ParseException::class)
    fun convertUTCToLocale(utcDateString: String, sourceFormat: String, designatedFormat: String): String {
        val dateFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val localDate = dateFormat.parse(utcDateString)

        val dateFormat1 = SimpleDateFormat(designatedFormat, Locale.getDefault())
        dateFormat1.timeZone = TimeZone.getDefault()
        return dateFormat1.format(localDate)
    }

    /*** Change Date Format  */
    @Throws(ParseException::class)
    fun changeDateFormat(utcDateString: String, sourceFormat: String, designatedForma: String): String {
        val dateFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())
        val localDate = dateFormat.parse(utcDateString)

        val dateFormat1 = SimpleDateFormat(designatedForma, Locale.getDefault())
        dateFormat1.timeZone = TimeZone.getDefault()
        return dateFormat1.format(localDate)
    }

    @Throws(ParseException::class)
    fun convertToLocaleNoSecond(utcDateString: String): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val localDate = dateFormat.parse(utcDateString)

        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(localDate)
    }

    /**
     * Copy file to given destination from source.
     *
     * @param sourceUrl      File path to copy
     * @param destinationUrl File path to save
     */
    @Throws(Exception::class)
    fun copyFile(sourceUrl: String, destinationUrl: String) {

        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {

            // create output directory if it doesn't exist
            if (!TextUtils.isEmpty(destinationUrl)) {
                val dir = File(destinationUrl.substring(0,
                        destinationUrl.lastIndexOf('/')))
                if (!dir.exists()) {
                    dir.mkdirs()
                }
            }

            `in` = FileInputStream(sourceUrl)
            out = FileOutputStream(destinationUrl)

            val buffer = ByteArray(CommonConstant.BUFFER_SIZE)
            var read: Int
            while ((read = `in`.read(buffer)) != -1) {
                out.write(buffer, 0, read)
            }
            out.flush()

        } finally {
            try {
                `in`?.close()

                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Read image file.
     *
     * @param photoUrl Image file path to read
     * @return Image bitmap
     */
    fun readImageFile(photoUrl: String): Bitmap? {
        var `in`: FileInputStream? = null
        val buf: BufferedInputStream

        var bmp: Bitmap? = null
        try {
            `in` = FileInputStream(photoUrl)
            buf = BufferedInputStream(`in`, CommonConstant.BUFFER_INPUT_SIZE)
            val buffer = ByteArray(buf.available())
            buf.read(buffer)
            bmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
        return bmp
    }

    /**
     * Saved Image file at given file path.
     *
     * @param bmp      Image's Bit Map
     * @param localUrl File path with file name
     */
    fun saveImageFile(bmp: Bitmap, localUrl: String) {
        var out: FileOutputStream? = null

        // create output directory if it doesn't exist
        if (!TextUtils.isEmpty(localUrl)) {
            val dir = File(localUrl.substring(0,
                    localUrl.lastIndexOf('/')))
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }

        try {
            out = FileOutputStream(localUrl)
            bmp.compress(Bitmap.CompressFormat.JPEG,
                    CommonConstant.IMAGE_QUALITY, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun isFileExists(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }

        val f = File(path)
        return f.exists()
    }

    fun encodeFileToBase64(url: String): String? {
        if (TextUtils.isEmpty(url)) {
            return null
        }

        var `in`: FileInputStream? = null
        val buf: BufferedInputStream
        var out: FileOutputStream? = null
        var inputStream: InputStream? = null
        val output = ByteArrayOutputStream()
        try {
            if (!url.endsWith(".amr")) {
                `in` = FileInputStream(url)
                buf = BufferedInputStream(`in`, CommonConstant.BUFFER_SIZE)
                val buffer = ByteArray(buf.available())
                buf.read(buffer)
                val fixValue = 300
                val widthValue: Int
                val heightValue: Int

                val bmp = BitmapFactory.decodeByteArray(buffer, 0,
                        buffer.size)
                if (bmp.width > bmp.height) {
                    val ratio = bmp.height.toFloat() / bmp.width.toFloat()
                    heightValue = (fixValue * ratio).toInt()
                    widthValue = fixValue
                } else {
                    val ratio = bmp.width.toFloat() / bmp.height.toFloat()
                    widthValue = (fixValue * ratio).toInt()
                    heightValue = fixValue
                }
                val rotateMatrix = rotateImage(url)
                val scaled = Bitmap.createScaledBitmap(bmp,
                        widthValue,
                        heightValue, true)

                val rotateBitmap = Bitmap.createBitmap(scaled, 0, 0,
                        scaled.width,
                        scaled.height, rotateMatrix, true)
                out = FileOutputStream(TEMP_PHOTO_URL)
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG,
                        CommonConstant.IMAGE_QUALITY, out)
                bmp.recycle()
            }

            inputStream = FileInputStream(url)
            val buffer1 = ByteArray(8192)
            var bytesRead: Int
            val output64 = Base64OutputStream(output,
                    Base64.DEFAULT)
            while ((bytesRead = inputStream.read(buffer1)) != -1) {
                output64.write(buffer1, 0, bytesRead)
            }

            inputStream.close()
            output64.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()

                out?.close()

                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return output.toString()
    }

    fun rotateImage(filePath: String): Matrix {
        val matrix = Matrix()

        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1)

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270f)
            }
        } catch (exception: Exception) {
            Log.d("Rotate Imgae", "Can't rotate image")
        }

        return matrix
    }

    @Throws(ParseException::class)
    fun isScheduledTrialClassExpired(utcDateString: String): Boolean {
        val dateFormat = SimpleDateFormat(FORMAT_NORMAL, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val localDate = dateFormat.parse(utcDateString)

        val scheduledDate = Calendar.getInstance()
        scheduledDate.time = localDate

        val calendarDate = Calendar.getInstance()
        calendarDate.time = Date()

        val isExpired: Boolean
        if (calendarDate.compareTo(scheduledDate) > 0) {
            isExpired = true
        } else {
            isExpired = false
        }

        return isExpired
    }

    /*** Generating MD5 key for Server Request String  */
    fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest.getInstance("MD5")
            val messageDigest = digest.digest(s.toByteArray(charset("UTF-8")))
            Log.d("TEST", "Byte Value = " + Arrays.toString(messageDigest))

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest)
                hexString.append(Integer.toHexString(0xFF and aMessageDigest))

            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ""
    }

    fun base64(dataValue: String): String {
        var encodeValue = ByteArray(0)
        try {
            encodeValue = Base64.encode(dataValue.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        var encodedString = String(encodeValue)
        encodedString = encodedString.replace("\n", "")
        Log.d("TEST", "Encoded Value = $encodedString")

        return encodedString
    }
}
