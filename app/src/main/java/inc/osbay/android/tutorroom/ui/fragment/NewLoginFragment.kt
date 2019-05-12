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
import android.widget.TextView
import android.widget.Toast

import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.ui.activity.MainActivity
import inc.osbay.android.tutorroom.utils.CommonUtil
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class NewLoginFragment : BackHandledFragment() {
    @BindView(R.id.password)
    internal var passET: EditText? = null
    @BindView(R.id.email)
    internal var emailET: EditText? = null
    @BindView(R.id.email_error_tv)
    internal var emailErrorTV: TextView? = null
    @BindView(R.id.password_error_tv)
    internal var passErrorTV: TextView? = null
    @BindView(R.id.forget_password)
    internal var forgetPassET: TextView? = null


    internal var sharedPreferenceData: SharedPreferenceData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_login, container, false)
        ButterKnife.bind(this, view)

        sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        return view
    }

    @OnClick(R.id.signup)
    internal fun clickHaveNoAccount() {
        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, SignupFragment()).commit()
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, SignupFragment()).commit()
        }
    }

    @OnClick(R.id.login)
    internal fun clickLogin() {
        val passwordSt = passET!!.text.toString()
        val emailSt = emailET!!.text.toString()

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

        //*** Password Validation ***/
        if (passwordSt == "") {
            passErrorTV!!.visibility = View.VISIBLE
            passErrorTV!!.text = getString(R.string.pass_error)
        } else {
            if (passwordSt.length < 6) {
                passErrorTV!!.visibility = View.VISIBLE
                passErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (passwordSt.length >= 6 && passwordSt.length <= 10) {
                passErrorTV!!.visibility = View.GONE
            }
        }

        if (emailSt != "" && CommonUtil.validateEmail(emailSt) && passwordSt != "" &&
                passwordSt.length >= 6 && passwordSt.length <= 10) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()

            val requestManager = ServerRequestManager(activity)
            requestManager.loginStudent(passwordSt, emailSt, object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    progressDialog.dismiss()
                    Log.i("Login", "Successful")
                    sharedPreferenceData.addBoolean("login", true)
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity.finish()
                }

                override fun onError(err: ServerError) {
                    progressDialog.dismiss()
                    Toast.makeText(activity, err.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    @OnClick(R.id.forget_password)
    internal fun forgetPassword() {
        //        Intent forgetIntent = new Intent(getActivity(), ForgetPasswordActivity.class);
        //        startActivity(forgetIntent);
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}