package inc.osbay.android.tutorroom.ui.activity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.ui.fragment.BackHandledFragment
import inc.osbay.android.tutorroom.ui.fragment.ClassroomFAQFragment
import inc.osbay.android.tutorroom.ui.fragment.ExistingLoginFragment
import inc.osbay.android.tutorroom.ui.fragment.ForgetPasswordFragment
import inc.osbay.android.tutorroom.ui.fragment.LeftMenuDrawerFragment
import inc.osbay.android.tutorroom.ui.fragment.LessonFragment
import inc.osbay.android.tutorroom.ui.fragment.OnlineSupportFragment
import inc.osbay.android.tutorroom.ui.fragment.PackageFragment
import inc.osbay.android.tutorroom.ui.fragment.ScheduleFragment
import inc.osbay.android.tutorroom.ui.fragment.SignupFragment
import inc.osbay.android.tutorroom.ui.fragment.StoreFragment
import inc.osbay.android.tutorroom.ui.fragment.TutorListFragment
import inc.osbay.android.tutorroom.ui.fragment.WebviewFragment

class FragmentHolderActivity : AppCompatActivity(), BackHandledFragment.BackHandlerInterface {
    @BindView(R.id.framelayout)
    internal var mFrameLayout: FrameLayout? = null
    internal var newFragment: Fragment? = null
    private var selectedFragment: BackHandledFragment? = null
    private var fragmentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_holder)
        ButterKnife.bind(this)

        fragmentName = intent.getStringExtra(EXTRA_DISPLAY_FRAGMENT)
        if (WelcomeActivity::class.java!!.getSimpleName() == fragmentName)
            newFragment = SignupFragment()
        else if (LeftMenuDrawerFragment::class.java!!.getSimpleName() == fragmentName || SplashActivity::class.java!!.getSimpleName() == fragmentName)
            newFragment = ExistingLoginFragment()
        else if (ClassroomFAQFragment::class.java!!.getSimpleName() == fragmentName) {
            newFragment = ClassroomFAQFragment()
            val bundle = Bundle()
            bundle.putString(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT, intent.getStringExtra(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT))
            newFragment!!.arguments = bundle
        } else if (TutorListFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = TutorListFragment()
        else if (ScheduleFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = ScheduleFragment()
        else if (StoreFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = StoreFragment()
        else if (PackageFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = PackageFragment()
        else if (LessonFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = LessonFragment()
        else if (OnlineSupportFragment::class.java!!.getSimpleName() == fragmentName)
            newFragment = OnlineSupportFragment()
        else if (WebviewFragment::class.java!!.getSimpleName() == fragmentName) {
            newFragment = WebviewFragment()
            val bundle = Bundle()
            bundle.putString(WebviewFragment.WEBVIEW_EXTRA, intent.getStringExtra(WebviewFragment.WEBVIEW_EXTRA))
            bundle.putString(WebviewFragment.TITLE_EXTRA, intent.getStringExtra(WebviewFragment.TITLE_EXTRA))
            newFragment!!.arguments = bundle
        } else if (ForgetPasswordFragment::class.java!!.getSimpleName() == fragmentName) {
            newFragment = ForgetPasswordFragment()
        }


        if (newFragment != null) {
            val fm = fragmentManager
            val fragment = fm.findFragmentById(R.id.framelayout)
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, newFragment).commit()
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, newFragment).commit()
            }
        } else {
            this@FragmentHolderActivity.finish()
        }
    }


    override fun setmSelectedFragment(backHandledFragment: BackHandledFragment) {
        if (selectedFragment != null) {
        }
        selectedFragment = backHandledFragment
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        val EXTRA_DISPLAY_FRAGMENT = "FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT"
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (WebviewFragment.class.getSimpleName().equals(fragmentName)) {
            WebviewFragment.onMyKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }*/
}