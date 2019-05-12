package inc.osbay.android.tutorroom.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.service.MessengerService
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class SplashActivity : AppCompatActivity() {
    internal var sharedPreferenceData: SharedPreferenceData
    internal var SPLASH_TIME_OUT = 2000
    @BindView(R.id.progress_bar)
    internal var mProgressBar: ProgressBar? = null
    @BindView(R.id.error)
    internal var errorTV: TextView? = null
    private var login: Boolean = false
    private var mAccount: Account? = null
    private var requestManager: ServerRequestManager? = null
    private val PERMISSION_REQUEST_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!checkPermissionForSDCard()) {
                requestPermissionForSDCard()
            } else {
                getCompanyConfig()
            }
        } else {
            getCompanyConfig()
        }
    }

    private fun checkPermissionForSDCard(): Boolean {
        val resultReadSD = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val resultWriteSD = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return resultReadSD == PackageManager.PERMISSION_GRANTED && resultWriteSD == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForSDCard() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, getString(R.string.storage_perm), Toast.LENGTH_LONG).show();
            errorTV!!.visibility = View.VISIBLE
            errorTV!!.text = getString(R.string.storage_perm)
            mProgressBar!!.visibility = View.GONE
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start to show other layouts.
                getCompanyConfig()
            } else {
                // Permission request was denied.
                errorTV!!.visibility = View.VISIBLE
                errorTV!!.text = getString(R.string.storage_perm)
                mProgressBar!!.visibility = View.GONE

            }
        }
    }

    fun getCompanyConfig() {
        mProgressBar!!.visibility = View.VISIBLE
        sharedPreferenceData = SharedPreferenceData(this@SplashActivity)
        requestManager = ServerRequestManager(this@SplashActivity)
        Handler().postDelayed({
            login = sharedPreferenceData.getBoolean("login")
            requestManager!!.getCompanyConfiguration(object : ServerRequestManager.OnRequestFinishedListener {
                override fun onSuccess(result: ServerResponse?) {
                    getThirdPartConfig()
                }

                override fun onError(err: ServerError) {
                    mProgressBar!!.visibility = View.GONE
                    errorTV!!.visibility = View.VISIBLE
                    errorTV!!.text = getString(R.string.check_internet)
                    Log.i("Get Config Failed", err.message)
                }
            })
        }, SPLASH_TIME_OUT.toLong())
    }

    fun getThirdPartConfig() {
        requestManager!!.getThirdPartConfig(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (login) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val DBAdapter = DBAdapter(this@SplashActivity)
                    val mPreferences = SharedPreferenceData(this@SplashActivity)
                    val accountId = mPreferences.getInt("account_id").toString()
                    mAccount = DBAdapter.getAccountById(accountId)

                    if (mAccount != null) {
                        val intent = Intent(this@SplashActivity, FragmentHolderActivity::class.java)
                        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, SplashActivity::class.java!!.getSimpleName())
                        startActivity(intent)
                    } else {
                        val i = Intent(this@SplashActivity, WelcomeActivity::class.java)
                        startActivity(i)
                    }
                    finish()
                }
                mProgressBar!!.visibility = View.GONE
            }

            override fun onError(err: ServerError) {
                mProgressBar!!.visibility = View.GONE
                errorTV!!.visibility = View.VISIBLE
                errorTV!!.text = getString(R.string.check_internet)
            }
        })
    }
}