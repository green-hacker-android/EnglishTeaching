package inc.osbay.android.tutorroom.ui.fragment

import android.app.AlertDialog
import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.util.Locale
import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class LeftMenuDrawerFragment : BackHandledFragment() {

    @BindView(R.id.sdv_profile_photo)
    internal var profileImg: ImageView? = null
    @BindView(R.id.noti_count)
    internal var notiCount: TextView? = null
    internal var sharedPreferenceData: SharedPreferenceData
    private var mLocale: String? = null
    private var mAccount: Account? = null
    private var count: Int = 0
    private var DBAdapter: DBAdapter? = null
    private var accountId: String? = null

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocale = Locale.getDefault().language
        sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        DBAdapter = DBAdapter(activity)
        accountId = sharedPreferenceData.getInt("account_id").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
                R.layout.fragment_left_menu_drawer, container, false)
        ButterKnife.bind(this, rootView)


        return rootView
    }

    override fun onResume() {
        super.onResume()
        mAccount = DBAdapter!!.getAccountById(accountId!!)
        count = DBAdapter!!.notiCount
        profileImg!!.setImageURI(Uri.parse(mAccount!!.avatar))
        if (count > 0) {
            notiCount!!.text = count.toString()
            notiCount!!.visibility = View.VISIBLE
        }
    }

    @OnClick(R.id.sdv_profile_photo)
    internal fun clickProfileImg() {
        val fm = parentFragment.fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = ProfileFragment()
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
        (parentFragment as MainFragment).closeDrawer()
    }

    @OnClick(R.id.noti_ll)
    internal fun clickNoti() {
        val fm = parentFragment.fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = NotificationFragment()
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
        (parentFragment as MainFragment).closeDrawer()
    }

    @OnClick(R.id.setting_tv)
    internal fun clickSetting() {
        val fm = parentFragment.fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = SettingFragment()
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
        (parentFragment as MainFragment).closeDrawer()
    }

    @OnClick(R.id.faq_tv)
    internal fun clickFAQ() {
        val fm = parentFragment.fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = ClassroomFAQFragment()

        val bundle = Bundle()
        bundle.putString(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT, LeftMenuDrawerFragment::class.java!!.getSimpleName())
        fragment.arguments = bundle
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
        (parentFragment as MainFragment).closeDrawer()
    }

    @OnClick(R.id.signout_tv)
    internal fun clickLogout() {
        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.signout))
                .setMessage(getString(R.string.lmd_sign_out_msg))
                .setNegativeButton(getString(R.string.cr_leave_room_cancel), null)
                .setPositiveButton(getString(R.string.signout)) { dialogInterface, i ->
                    sharedPreferenceData.addBoolean("login", false)

                    val intent = Intent(activity, FragmentHolderActivity::class.java)
                    intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, LeftMenuDrawerFragment::class.java!!.getSimpleName())
                    startActivity(intent)
                    activity.finish()
                }
                .show()
    }
}