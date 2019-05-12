package inc.osbay.android.tutorroom.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.view.SimpleDraweeView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import org.json.JSONArray
import org.json.JSONException

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.CountryCodeAdapter
import inc.osbay.android.tutorroom.adapter.LanguageAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.CountryCode
import inc.osbay.android.tutorroom.sdk.model.Language
import inc.osbay.android.tutorroom.sdk.util.LGCUtil
import inc.osbay.android.tutorroom.ui.activity.CameraActivity
import inc.osbay.android.tutorroom.utils.ImageFilePath
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class EditProfileFragment : BackHandledFragment() {
    @BindView(R.id.country_code_spinner)
    internal var mSpnAccountPhCodes: Spinner? = null
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.name_ed)
    internal var nameTv: EditText? = null
    @BindView(R.id.email_ed)
    internal var emailTv: EditText? = null
    @BindView(R.id.phone_ed)
    internal var phoneTV: EditText? = null
    @BindView(R.id.country_ed)
    internal var countryTv: EditText? = null
    @BindView(R.id.address_ed)
    internal var addressTv: EditText? = null
    @BindView(R.id.native_lang_spinner)
    internal var nativeLangTv: Spinner? = null
    @BindView(R.id.confirm_tv)
    internal var confirmTv: TextView? = null
    @BindView(R.id.sdv_profile_photo)
    internal var profilePic: SimpleDraweeView? = null
    internal var sharedPreferenceData: SharedPreferenceData
    private var progressDialog: ProgressDialog? = null
    private var mRequestManager: ServerRequestManager? = null
    private var mAccount: Account? = null
    private var countryCodeAdapter: CountryCodeAdapter? = null
    private var accountId: String? = null
    private val languageList = ArrayList<Language>()
    private var languageAdapter: LanguageAdapter? = null
    private var mDBAdapter: DBAdapter? = null
    private var nativeLang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull(activity))
        mRequestManager = ServerRequestManager(activity)

        mDBAdapter = DBAdapter(activity)
        accountId = sharedPreferenceData.getInt("account_id").toString()
        mAccount = mDBAdapter!!.getAccountById(accountId!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage(getString(R.string.loading))
        progressDialog!!.show()
        mRequestManager!!.getLanguageList(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                progressDialog!!.dismiss()
                if (response!!.code == 1) {
                    try {
                        val languageArray = JSONArray(response.dataSt)
                        for (i in 0 until languageArray.length()) {
                            val language = Language(languageArray.getJSONObject(i))
                            languageList.add(language)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    languageAdapter = LanguageAdapter(activity, languageList)
                    val mCountryCodes = mDBAdapter!!.countryCodes
                    countryCodeAdapter = CountryCodeAdapter(activity, mCountryCodes)

                    mSpnAccountPhCodes!!.adapter = countryCodeAdapter
                    nativeLangTv!!.adapter = languageAdapter
                    profilePic!!.setImageURI(Uri.parse(mAccount!!.avatar))
                    nameTv!!.setText(mAccount!!.name)
                    emailTv!!.setText(mAccount!!.email)
                    for (i in 0 until mSpnAccountPhCodes!!.adapter.count) {
                        if (mSpnAccountPhCodes!!.adapter.getItem(i).toString().contains(mAccount!!.phoneCode!!)) {
                            mSpnAccountPhCodes!!.setSelection(i)
                            break
                        }
                    }
                    phoneTV!!.setText(mAccount!!.phoneNumber)

                    if (mAccount!!.country != "null" && mAccount!!.country != "")
                        countryTv!!.setText(mAccount!!.country)
                    else
                        countryTv!!.setText("")
                    if (mAccount!!.address != "null" && mAccount!!.address != "")
                        addressTv!!.setText(mAccount!!.address)
                    else
                        addressTv!!.setText("")

                    if (mAccount!!.speakingLang != "null" && mAccount!!.speakingLang != "") {
                        for (i in 0 until nativeLangTv!!.adapter.count) {
                            if (nativeLangTv!!.adapter.getItem(i).toString().equals(mAccount!!.speakingLang!!, ignoreCase = true)) {
                                nativeLangTv!!.setSelection(i)
                                break
                            }
                        }
                    }
                }
            }

            override fun onError(err: ServerError) {
                progressDialog!!.dismiss()
                Toast.makeText(activity, err.message, Toast.LENGTH_LONG).show()
            }
        })
        nativeLangTv!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                nativeLang = languageList[i].languageName
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    @OnClick(R.id.confirm_tv)
    internal fun clickSaveProfile() {
        progressDialog!!.show()

        val name = nameTv!!.text.toString()
        val email = emailTv!!.text.toString()
        val countryCodeSt = mSpnAccountPhCodes!!.selectedItem.toString()
        val phone = phoneTV!!.text.toString()
        val country = countryTv!!.text.toString()
        val address = addressTv!!.text.toString()

        mRequestManager!!.updateProfileInfo(accountId, name, countryCodeSt, phone, email, address,
                country, nativeLang, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                getStudentInfo(accountId, progressDialog)
            }

            override fun onError(err: ServerError) {
                progressDialog!!.dismiss()
                Log.i("Update Failed", err.message)
            }
        })
    }

    private fun getStudentInfo(studentID: String?, progressDialog: ProgressDialog?) {
        mRequestManager!!.getProfileInfo(studentID!!, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                progressDialog!!.dismiss()
                fragmentManager.popBackStack()
            }

            override fun onError(err: ServerError) {
                progressDialog!!.dismiss()
                Log.i("Profile Failed", err.message)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onStart() {
        super.onStart()
        setTitle(getString(R.string.profile))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    @OnClick(R.id.profile_pic_rl)
    internal fun clickProfilePic() {
        showChoosePhotoDialog()
    }

    private fun showChoosePhotoDialog() {
        // custom dialog
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_choose_photo)

        val cameraButton = dialog.findViewById<TextView>(R.id.tv_camera)
        cameraButton.setOnClickListener { arg0 ->
            // start default camera
            val cameraIntent = Intent(activity,
                    CameraActivity::class.java)
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                    TEMP_PHOTO_URL)
            startActivityForResult(cameraIntent, TAKE_PICTURE)
            dialog.dismiss()
        }

        val galleryButton = dialog.findViewById<TextView>(R.id.tv_gallery)
        galleryButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                val intent = Intent()
                intent.type = "image/jpeg"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.ac_info_choose)),
                        CHOOSE_PICTURE)
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpeg"
                startActivityForResult(intent, CHOOSE_PICTURE)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val params = HashMap<String, String>()

        when (requestCode) {
            CHOOSE_PICTURE -> {
                val originalUri: Uri?
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    originalUri = data.data
                } else {
                    originalUri = data.data
                    // Check for the freshest data.
                    activity.contentResolver
                            .takePersistableUriPermission(originalUri!!,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                try {
                    Log.e("Image URI - ", originalUri!!.toString())
                    LGCUtil.copyFile(
                            ImageFilePath.getPath(activity,
                                    originalUri)!!,
                            TEMP_PHOTO_URL)

                    CropImage.activity(Uri.fromFile(File(TEMP_PHOTO_URL)))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(activity)

                } catch (e: Exception) {
                    Log.e(TAG, "Photo unreadable - ", e)
                    Toast.makeText(activity,
                            getString(R.string.ac_no_photo_url),
                            Toast.LENGTH_SHORT).show()
                }

                return
            }
            TAKE_PICTURE -> {
                CropImage.activity(Uri.fromFile(File(TEMP_PHOTO_URL)))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(activity)
                return
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                params["Field"] = "Photo"

                val result = CropImage.getActivityResult(data)
                val croppedUri = result.uri

                try {
                    Log.e("Cropped URI - ", croppedUri.toString())
                    LGCUtil.copyFile(
                            ImageFilePath.getPath(activity,
                                    croppedUri)!!,
                            TEMP_PHOTO_URL)

                    if (uploadImageToServer()) return

                } catch (e: Exception) {
                    Log.e(TAG, "Photo unreadable - ", e)
                    Toast.makeText(activity,
                            getString(R.string.ac_no_photo_url),
                            Toast.LENGTH_SHORT).show()
                }

            }
            else -> return
        }
    }

    private fun uploadImageToServer(): Boolean {
        val fixValue = 300
        val widthValue: Int
        val heightValue: Int

        val bmp = LGCUtil
                .readImageFile(TEMP_PHOTO_URL)
        if (bmp == null) {
            Toast.makeText(activity,
                    getString(R.string.ac_no_photo_url),
                    Toast.LENGTH_SHORT).show()
            return true
        }

        if (bmp.width > bmp.height) {
            val ratio = bmp.height.toFloat() / bmp.width.toFloat()
            heightValue = (fixValue * ratio).toInt()
            widthValue = fixValue
        } else {
            val ratio = bmp.width.toFloat() / bmp.height.toFloat()
            widthValue = (fixValue * ratio).toInt()
            heightValue = fixValue
        }

        val scaled = Bitmap.createScaledBitmap(bmp,
                widthValue,
                heightValue, true)

        LGCUtil.saveImageFile(scaled, TEMP_PHOTO_URL)

        uploadImage(scaled, TEMP_PHOTO_URL)

        bmp.recycle()
        return false
    }

    private fun uploadImage(scaled: Bitmap, imgPath: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()

        //encode image to base64 string
        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        mRequestManager!!.updateAvatar(mAccount!!.accountId.toString(), imageString, object : ServerRequestManager.OnRequestFinishedListener {

            override fun onSuccess(obj: ServerResponse?) {
                progressDialog.dismiss()
                if (activity != null) {
                    if (obj != null) {
                        mAccount!!.avatar = obj.toString()

                        try {
                            changeProfileImage()
                        } catch (e: Exception) {
                            Log.e(TAG, "Cannot read Image file", e)
                        }

                    } else {
                        Log.e(TAG,
                                "Update Image link is null")
                    }
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
                if (activity != null) {
                    if (!TextUtils.isEmpty(mAccount!!.avatar)) {
                        changeProfileImage()
                    }
                }
            }
        })
    }

    private fun changeProfileImage() {
        if (!TextUtils.isEmpty(mAccount!!.avatar)) {
            profilePic!!.setImageURI(Uri.parse(mAccount!!.avatar))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    companion object {

        val TEMP_PHOTO_URL = CommonConstant.IMAGE_PATH + File.separator + "temp.jpg"
        private val TAG = ProfileFragment::class.java!!.getSimpleName()
        private val TAKE_PICTURE = 111
        private val CHOOSE_PICTURE = 112
    }
}
