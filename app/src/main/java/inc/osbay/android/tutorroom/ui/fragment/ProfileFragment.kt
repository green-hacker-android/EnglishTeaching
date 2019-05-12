package inc.osbay.android.tutorroom.ui.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.app.FragmentManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Account
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class ProfileFragment : BackHandledFragment() {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.name_tv)
    internal var nameTv: TextView? = null
    @BindView(R.id.email_tv)
    internal var emailTv: TextView? = null
    @BindView(R.id.phone_tv)
    internal var phoneTV: TextView? = null
    @BindView(R.id.country_tv)
    internal var countryTv: TextView? = null
    @BindView(R.id.address_tv)
    internal var addressTv: TextView? = null
    @BindView(R.id.native_lang_tv)
    internal var nativeLangTv: TextView? = null
    @BindView(R.id.change_pwd_tv)
    internal var changePwdTv: TextView? = null
    @BindView(R.id.edit_tv)
    internal var editTv: TextView? = null
    @BindView(R.id.sdv_profile_photo)
    internal var profilePic: SimpleDraweeView? = null
    /*@BindView(R.id.noti_img)
    ImageView notiImg;*/
    private var mAccount: Account? = null
    private var mDBAdapter: DBAdapter? = null
    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDBAdapter = DBAdapter(activity)
        val mPreferences = SharedPreferenceData(activity)
        accountId = mPreferences.getInt("account_id").toString()
        mAccount = mDBAdapter!!.getAccountById(accountId!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profilePic!!.setImageURI(Uri.parse(mAccount!!.avatar))
        nameTv!!.text = mAccount!!.name
        emailTv!!.text = mAccount!!.email
        phoneTV!!.text = mAccount!!.phoneCode + "" + mAccount!!.phoneNumber
        if (mAccount!!.country != "null" && mAccount!!.country != "")
            countryTv!!.text = mAccount!!.country
        else
            countryTv!!.text = ""
        if (mAccount!!.address != "null" && mAccount!!.address != "")
            addressTv!!.text = mAccount!!.address
        else
            addressTv!!.text = ""
        nativeLangTv!!.text = mAccount!!.speakingLang
        //notiImg.setImageDrawable(CommonUtil.createImage(getActivity(), count));
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

    override fun onResume() {
        super.onResume()
        //notiImg.setImageDrawable(CommonUtil.createImage(getActivity(), count));
        mAccount = mDBAdapter!!.getAccountById(accountId!!)
        profilePic!!.setImageURI(Uri.parse(mAccount!!.avatar))
        nameTv!!.text = mAccount!!.name
        emailTv!!.text = mAccount!!.email
        phoneTV!!.text = mAccount!!.phoneCode!! + mAccount!!.phoneNumber!!
        /*countryTv.setText(mAccount.getCountry());
        addressTv.setText(mAccount.getAddress());*/
        if (mAccount!!.country != "null" && mAccount!!.country != "")
            countryTv!!.text = mAccount!!.country
        else
            countryTv!!.text = ""
        if (mAccount!!.address != "null" && mAccount!!.address != "")
            addressTv!!.text = mAccount!!.address
        else
            addressTv!!.text = ""
        nativeLangTv!!.text = mAccount!!.speakingLang
    }

    /*@OnClick(R.id.noti_img)
    void readNotification() {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new NotificationFragment();
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
    }*/

    @OnClick(R.id.edit_tv)
    internal fun clickEdit() {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = EditProfileFragment()
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
    }

    @OnClick(R.id.change_pwd_tv)
    internal fun changePassword() {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = PasswordFragment()
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}
