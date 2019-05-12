package inc.osbay.android.tutorroom.ui.activity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log

import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.TRApplication
import inc.osbay.android.tutorroom.service.MessengerService
import inc.osbay.android.tutorroom.ui.fragment.BackHandledFragment
import inc.osbay.android.tutorroom.ui.fragment.MainFragment
import inc.osbay.android.tutorroom.utils.SharedPreferenceData
import inc.osbay.android.tutorroom.utils.WSMessageClient

class MainActivity : AppCompatActivity(), BackHandledFragment.BackHandlerInterface {

    private var selectedFragment: BackHandledFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_holder)
        ButterKnife.bind(this)


        val sharedPreferenceData = SharedPreferenceData(this)
        val WSMessageClient = (application as TRApplication).wsMessageClient
        val message = Message.obtain(null, MessengerService.MSG_WS_LOGIN)
        val bundle = Bundle()
        bundle.putString("user_id", "S_" + sharedPreferenceData.getInt("account_id"))
        message.data = bundle
        try {
            WSMessageClient!!.sendMessage(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fm = fragmentManager
        val fragment = fm.findFragmentById(R.id.framelayout)
        val mainFragment = MainFragment()
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, mainFragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, mainFragment)
                    .commit()
        }
    }

    override fun setmSelectedFragment(backHandledFragment: BackHandledFragment) {
        if (selectedFragment != null) {
        }
        selectedFragment = backHandledFragment
    }
}
