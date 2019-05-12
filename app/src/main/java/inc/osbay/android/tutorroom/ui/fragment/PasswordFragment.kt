package inc.osbay.android.tutorroom.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
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
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class PasswordFragment : BackHandledFragment() {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.old_pass)
    internal var oldPassET: EditText? = null
    @BindView(R.id.confirm_new_pass)
    internal var confirmNewPassET: EditText? = null
    @BindView(R.id.new_pass)
    internal var newPassET: EditText? = null
    @BindView(R.id.old_pass_error_tv)
    internal var oldPassErrorTV: TextView? = null
    @BindView(R.id.pass_error_tv)
    internal var passErrorTV: TextView? = null
    @BindView(R.id.confirm_pass_error_tv)
    internal var confirmPassErrorTV: TextView? = null
    internal var sharedPreferenceData: SharedPreferenceData
    private var mRequestManager: ServerRequestManager? = null
    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        mRequestManager = ServerRequestManager(activity)
        accountId = sharedPreferenceData.getInt("account_id").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onStart() {
        super.onStart()
        setTitle(getString(R.string.change_pass))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    @OnClick(R.id.change_pass)
    internal fun changePass() {
        val oldPass = oldPassET!!.text.toString()
        val newPass = newPassET!!.text.toString()
        val confirmNewPass = confirmNewPassET!!.text.toString()

        if (oldPass == "") {
            oldPassErrorTV!!.visibility = View.VISIBLE
            oldPassErrorTV!!.text = getString(R.string.old_pass_error)
        } else {
            if (oldPass.length < 6) {
                oldPassErrorTV!!.visibility = View.VISIBLE
                oldPassErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (oldPass.length >= 6 && oldPass.length <= 10) {
                oldPassErrorTV!!.visibility = View.GONE
            }
        }

        if (newPass != confirmNewPass) {
            passErrorTV!!.visibility = View.VISIBLE
            confirmPassErrorTV!!.visibility = View.VISIBLE
            passErrorTV!!.text = getString(R.string.pass_not_matched)
            confirmPassErrorTV!!.text = getString(R.string.pass_not_matched)
        } else if (newPass == confirmNewPass) {
            passErrorTV!!.visibility = View.GONE
            confirmPassErrorTV!!.visibility = View.GONE
        }

        if (newPass == "") {
            passErrorTV!!.visibility = View.VISIBLE
            passErrorTV!!.text = getString(R.string.new_pass_error)
        } else {
            if (newPass.length < 6) {
                passErrorTV!!.visibility = View.VISIBLE
                passErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (newPass.length >= 6 && newPass.length <= 10 && newPass == confirmNewPass) {
                passErrorTV!!.visibility = View.GONE
            }
        }
        if (confirmNewPass == "") {
            confirmPassErrorTV!!.visibility = View.VISIBLE
            confirmPassErrorTV!!.text = getString(R.string.conf_new_pass_error)
        } else {
            if (confirmNewPass.length < 6) {
                confirmPassErrorTV!!.visibility = View.VISIBLE
                confirmPassErrorTV!!.text = getString(R.string.pass_min_length)
            }
            if (confirmNewPass.length >= 6 && confirmNewPass.length <= 10 && newPass == confirmNewPass) {
                confirmPassErrorTV!!.visibility = View.GONE
            }
        }

        if (oldPass != "" && oldPass.length >= 6 && oldPass.length <= 10 &&
                newPass == confirmNewPass && newPass != "" && newPass.length >= 6 &&
                newPass.length <= 10 && confirmNewPass != "" &&
                confirmNewPass.length >= 6 && confirmNewPass.length <= 10) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()

            mRequestManager!!.changePassword(accountId, oldPass, newPass,
                    object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(result: ServerResponse?) {
                            progressDialog.dismiss()
                            if (result!!.code == 1) {
                                Toast.makeText(activity,
                                        resources.getString(R.string.pwd_changed), Toast.LENGTH_LONG).show()
                                fragmentManager.popBackStack()
                            } else {
                                oldPassErrorTV!!.visibility = View.VISIBLE
                                oldPassErrorTV!!.text = result.message
                            }
                        }

                        override fun onError(err: ServerError) {
                            progressDialog.dismiss()
                            Toast.makeText(activity, err.message, Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}