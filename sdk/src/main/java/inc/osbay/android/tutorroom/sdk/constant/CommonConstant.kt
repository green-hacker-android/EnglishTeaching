package inc.osbay.android.tutorroom.sdk.constant

import android.os.Environment

import java.io.File

import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager

object CommonConstant {
    val DB_CREATE_FAIL = 1
    val TAG = ServerRequestManager::class.java!!.getSimpleName()

    /*** Engleezi  */
    val WEB_SERVICE_URL = "http://service.myengleezi.com/mobileservice.asmx/Execute"
    val WHITEBOARD_URL = "https://edu.myengleezi.com/ClassRoom/mBoard.htm"
    val PAYPAL_URL = "https://www.sandbox.paypal.com"

    /*** Sandbox  */
    /*public static final String WEB_SERVICE_URL = "http://s1.tutormandarin.net/mobileservice.asmx/Execute";
    public static final String WHITEBOARD_URL = "https://m1.tutormandarin.net/ClassRoom/mBoard.htm";*/

    val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    val APPLICATION_FOLDER = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Tutor Room"

    val MEDIA_PATH = APPLICATION_FOLDER + File.separator + "media"
    val IMAGE_PATH = APPLICATION_FOLDER + File.separator + "image"
    val LOG_PATH = APPLICATION_FOLDER + File.separator + "log"
    val PDF_PATH = APPLICATION_FOLDER + File.separator + "pdf"

    /**
     * Buffer Size.
     */
    val BUFFER_SIZE = 1024

    /**
     * Image Scale Size.
     */
    val BUFFER_INPUT_SIZE = 1070

    /**
     * Image Quality.
     */
    val IMAGE_QUALITY = 100

    val AGENT_PROMO_CODE_PREF = "AGENT_PROMO_CODE_PREF"
    val USE_PROMO_CODE_PREF = "USE_PROMO_CODE_PREF"

    /* Tag Type */
    val PackageTag = 1
    val LessonTag = 2
    val FlashCardTag = 3

    /* Buy Credit Type */
    val buyCredit = 1
    val buyStorePackage = 2

    /* Booking Type */
    val lessonBookingType = 1
    val packageBookingType = 2

    /* Lesson Type */
    val singleLessonType = 1
    val packageLessonType = 2

    /* Banner Type */
    val HomeBanner = 1
    val LeftBanner = 2
    val RightBanner = 3
    val TopBanner = 4
    val BottomBanner = 5
    val HomeBannerwithText = 6
    val MB_HomeBanner = 7
    val MB_HomeBannerwithText = 8
    val MB_LeftBanner = 9
    val MB_RightBanner = 10
    val MB_TopBanner = 11
    val MB_BottomBanner = 12

    /* Class Type */
    val Single = 1
    val Offline = 2
    val Multiple = 3
    val Trial = 4
    val Single_Trial = 5

    /* Register Type */
    val emailRegisterType = "1"
}
