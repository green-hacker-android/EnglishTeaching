package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.view.SimpleDraweeView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.ui.activity.MainActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class ExistingLoginFragment : BackHandledFragment() {

    @BindView(R.id.password)
    internal var passET: EditText? = null
    @BindView(R.id.name)
    internal var nameTV: TextView? = null
    @BindView(R.id.profile_pic_img)
    internal var profilePic: SimpleDraweeView? = null
    @BindView(R.id.password_error_tv)
    internal var passErrorTV: TextView? = null

    private var mAccount: Account? = null
    private var mPreferences: SharedPreferenceData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_existing_login, container, false)
        ButterKnife.bind(this, view)

        mPreferences = SharedPreferenceData(activity)
        val DBAdapter = DBAdapter(activity)
        val accountId = mPreferences!!.getInt("account_id").toString()
        mAccount = DBAdapter.getAccountById(accountId)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mAccount!!.avatar != null)
            profilePic!!.setImageURI(Uri.parse(mAccount!!.avatar))
        nameTV!!.text = mAccount!!.name
    }

    @OnClick(R.id.login)
    internal fun clickLogin() {
        val passwordSt = passET!!.text.toString()
        val emailSt = mAccount!!.email

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

        if (passwordSt != "" && passwordSt.length >= 6 && passwordSt.length <= 10) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()
            val requestManager = ServerRequestManager(activity)
            requestManager.loginStudent(passwordSt, emailSt!!, object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    progressDialog.dismiss()
                    Log.i("Login", "Successful")
                    mPreferences!!.addBoolean("login", true)
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

    @OnClick(R.id.change_user)
    internal fun changeUser() {
        Log.d("Change User", "Change User button click")
        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, NewLoginFragment()).commit()
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, NewLoginFragment()).commit()
        }
    }

    @OnClick(R.id.forget_password)
    internal fun forgetPassword() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ForgetPasswordFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}
