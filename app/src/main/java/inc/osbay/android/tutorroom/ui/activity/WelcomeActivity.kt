package inc.osbay.android.tutorroom.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager

class WelcomeActivity : AppCompatActivity() {
    @BindView(R.id.start)
    internal var startBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.start)
    internal fun clickStart() {
        val intent = Intent(this, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, WelcomeActivity::class.java!!.getSimpleName())
        startActivity(intent)
        finish()

        val requestManager = ServerRequestManager(this)

        /* requestManager.getTutorList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/
        /*requestManager.getTutorDetailByID("5", new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLanguageList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getCountryList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getPackageListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLessonListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLessonListByPackageID(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/
    }
}