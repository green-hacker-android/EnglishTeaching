package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.CountryCodeAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.sdk.model.CountryCode
import inc.osbay.android.tutorroom.ui.activity.MainActivity
import inc.osbay.android.tutorroom.utils.CommonUtil
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class SignupFragment : BackHandledFragment() {

    @BindView(R.id.country_code_spinner)
    internal var mSpnAccountPhCodes: Spinner? = null
    @BindView(R.id.name)
    internal var nameET: EditText? = null
    @BindView(R.id.password)
    internal var passET: EditText? = null
    @BindView(R.id.re_password)
    internal var rePassET: EditText? = null
    @BindView(R.id.phone_et)
    internal var phoneET: EditText? = null
    @BindView(R.id.email)
    internal var emailET: EditText? = null
    @BindView(R.id.name_error_tv)
    internal var nameErrorTV: TextView? = null
    @BindView(R.id.password_error_tv)
    internal var passErrorTV: TextView? = null
    @BindView(R.id.re_password_error_tv)
    internal var repassErrorTV: TextView? = null
    @BindView(R.id.email_error_tv)
    internal var emailErrorTV: TextView? = null
    @BindView(R.id.phone_error_tv)
    internal var phoneErrorTV: TextView? = null
    private var sharedPreferenceData: SharedPreferenceData? = null
    private var requestManager: ServerRequestManager? = null
    private var nameSt: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        ButterKnife.bind(this, view)

        sharedPreferenceData = SharedPreferenceData(activity)
        val DBAdapter = DBAdapter(activity)
        val countryCodes = DBAdapter.countryCodes

        val adapter = CountryCodeAdapter(activity, countryCodes)
        mSpnAccountPhCodes!!.adapter = adapter
        return view
    }

    @OnClick(R.id.login)
    internal fun clickLogin() {
        val DBAdapter = DBAdapter(activity)
        val mPreferences = SharedPreferenceData(activity)
        val accountId = mPreferences.getInt("account_id").toString()
        val account = DBAdapter.getAccountById(accountId)

        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)
        if (account != null) {
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, ExistingLoginFragment()).commit()
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, ExistingLoginFragment()).commit()
            }
        } else {
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, NewLoginFragment()).commit()
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, NewLoginFragment()).commit()
            }
        }
    }

    @OnClick(R.id.signup)
    internal fun clickSignup() {
        nameSt = nameET!!.text.toString()
        val passwordSt = passET!!.text.toString()
        val rePasswordSt = rePassET!!.text.toString()
        val emailSt = emailET!!.text.toString()
        val countryCodeSt = mSpnAccountPhCodes!!.selectedItem.toString()
        val phoneSt = phoneET!!.text.toString()
        requestManager = ServerRequestManager(activity)

        //*** Name Validation ***/
        if (nameSt == "") {
            nameErrorTV!!.visibility = View.VISIBLE
        } else {
            nameErrorTV!!.visibility = View.GONE
        }

        //*** Password Validation ***/
        if (passwordSt != rePasswordSt) {
            passErrorTV!!.visibility = View.VISIBLE
            repassErrorTV!!.visibility = View.VISIBLE
            passErrorTV!!.text = getString(R.string.pass_not_matched)
            repassErrorTV!!.text = getString(R.string.pass_not_matched)
        } else if (passwordSt == rePasswordSt) {
            passErrorTV!!.visibility = View.GONE
            repassErrorTV!!.visibility = View.GONE
        }

        if (passwordSt == "") {
            passErrorTV!!.visibility = View.VISIBLE
            passErrorTV!!.text = getString(R.string.pass_error)
        } else {
            if (passwordSt.length < 6) {
                passErrorTV!!.visibility = View.VISIBLE
                passErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (passwordSt.length >= 6 && passwordSt.length <= 10 && passwordSt == rePasswordSt) {
                passErrorTV!!.visibility = View.GONE
            }
        }
        if (rePasswordSt == "") {
            repassErrorTV!!.visibility = View.VISIBLE
            repassErrorTV!!.text = getString(R.string.repass_error)
        } else {
            if (rePasswordSt.length < 6) {
                repassErrorTV!!.visibility = View.VISIBLE
                repassErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (rePasswordSt.length >= 6 && rePasswordSt.length <= 10 && passwordSt == rePasswordSt) {
                repassErrorTV!!.visibility = View.GONE
            }
        }

        //*** Email Validation ***/
        if (emailSt == "") {
            emailErrorTV!!.visibility = View.VISIBLE
            emailErrorTV!!.text = getString(R.string.email_error)
        } else if (emailSt != "" && !CommonUtil.validateEmail(emailSt)) {
            emailErrorTV!!.visibility = View.VISIBLE
            emailErrorTV!!.text = getString(R.string.invalid_email)
        } else if (emailSt != "" && CommonUtil.validateEmail(emailSt)) {
            emailErrorTV!!.visibility = View.GONE
        }

        //*** Country Validation ***/
        if (countryCodeSt == "" || phoneSt == "") {
            phoneErrorTV!!.visibility = View.VISIBLE
            phoneErrorTV!!.text = getString(R.string.phone_error)
        } else {
            phoneErrorTV!!.visibility = View.GONE
        }

        if (nameSt != "" && passwordSt == rePasswordSt && passwordSt != "" &&
                passwordSt.length >= 6 && passwordSt.length <= 10 && rePasswordSt != "" &&
                rePasswordSt.length >= 6 && rePasswordSt.length <= 10 && emailSt != "" &&
                CommonUtil.validateEmail(emailSt) and (countryCodeSt != "") && phoneSt != "") {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()
            requestManager!!.registerStudent(nameSt!!, passwordSt, emailSt, countryCodeSt, phoneSt,
                    CommonConstant.emailRegisterType, object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    if (result!!.code == ServerResponse.Status.SUCCESS) {
                        try {
                            val `object` = JSONObject(result.dataSt)
                            val studentID = `object`.getString("account_id")
                            sharedPreferenceData!!.addInt("account_id", Integer.parseInt(studentID))
                            sharedPreferenceData!!.addString("account_name", nameSt)
                            getStudentInfo(studentID, progressDialog)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (result.code == ServerResponse.Status.Account_EmailAlreadyExit) {
                        progressDialog.dismiss()
                        emailErrorTV!!.visibility = View.VISIBLE
                        emailErrorTV!!.text = result.message
                    }
                }

                override fun onError(err: ServerError) {
                    progressDialog.dismiss()
                    Log.i("Register Failed", err.message)
                }
            })
        }
    }

    private fun getStudentInfo(studentID: String, progressDialog: ProgressDialog) {
        requestManager!!.getProfileInfo(studentID, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                progressDialog.dismiss()
                Log.i("Profile", "Received")
                sharedPreferenceData!!.addBoolean("login", true)
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity.finish()
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
                Log.i("Profile Failed", err.message)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}